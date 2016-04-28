package com.redhat.ceylon.compiler.typechecker.analyzer;

import static com.redhat.ceylon.compiler.typechecker.analyzer.AnalyzerUtil.getLastConstructor;
import static com.redhat.ceylon.compiler.typechecker.analyzer.AnalyzerUtil.getLastExecutableStatement;
import static com.redhat.ceylon.compiler.typechecker.analyzer.AnalyzerUtil.isAlwaysSatisfied;
import static com.redhat.ceylon.compiler.typechecker.analyzer.AnalyzerUtil.isAtLeastOne;
import static com.redhat.ceylon.compiler.typechecker.analyzer.AnalyzerUtil.isNeverSatisfied;
import static com.redhat.ceylon.compiler.typechecker.analyzer.AnalyzerUtil.message;
import static com.redhat.ceylon.compiler.typechecker.tree.TreeUtil.isEffectivelyBaseMemberExpression;
import static com.redhat.ceylon.compiler.typechecker.tree.TreeUtil.isSelfReference;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isConstructor;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isNativeHeader;

import java.util.ArrayList;
import java.util.List;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Constructor;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Function;
import com.redhat.ceylon.model.typechecker.model.FunctionOrValue;
import com.redhat.ceylon.model.typechecker.model.Parameter;
import com.redhat.ceylon.model.typechecker.model.Scope;
import com.redhat.ceylon.model.typechecker.model.Setter;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.model.typechecker.model.Value;

/**
 * Validates that non-variable values are well-defined
 * within the local scope in which they occur. Checks
 * that they are not used before they are defined, that
 * they are always specified before they are used, and
 * that they are never specified twice.
 * 
 * @author Gavin King
 *
 */
public class SpecificationVisitor extends Visitor {
    
    private final Declaration declaration;
    
    private SpecificationState specified = 
            new SpecificationState(false, false);
    private boolean specificationDisabled = false;
    private boolean withinDeclaration = false;
    private int loopDepth = 0;
    private int brokenLoopDepth = 0;
    private boolean allOuterLoopsBreak = true;
    private boolean declared = false;
    private boolean hasParameter = false;
    private Tree.Statement lastExecutableStatement;
    private Tree.Declaration lastConstructor;
    private boolean declarationSection = false;
    private boolean endsInReturnThrow = false;
    private boolean endsInBreak = false;
    private boolean inExtends = false;
    private boolean inAnonFunctionOrComprehension = false;
    private Parameter parameter = null;
    private boolean usedInDeclarationSection = false;
    private boolean definedInDeclarationSection = false;
    
    @Override
    public void visit(Tree.ExtendedType that) {
        boolean oie = inExtends;
        inExtends = declared;
        super.visit(that);
        inExtends = oie;
    }
    
    private final class ContinueVisitor extends Visitor {
        Tree.Continue node;
        boolean found;
        Tree.Statement lastContinue;
        ContinueVisitor(Tree.Statement lastContinue) {
           this.lastContinue = lastContinue;
           node = null;
           found = false;
        }
        @Override
        public void visit(Tree.Declaration that) {}
        @Override
        public void visit(Tree.WhileStatement that) {}
        @Override
        public void visit(Tree.ForStatement that) {}
        @Override
        public void visit(Tree.Continue that) {
            node = that;
            if (that==lastContinue) {
                found = true;
            }
        }
    }

    class SpecificationState {
        boolean definitely;
        boolean possibly;
        boolean possiblyExited;
        boolean definitelyExited;
        boolean definitelyByLoopBreaks;
        boolean possiblyByLoopBreaks;
        SpecificationState(boolean definitely, boolean possibly) {
            this.definitely = definitely;
            this.possibly = possibly;
            this.possiblyExited = false;
            this.definitelyExited = false;
            this.definitelyByLoopBreaks = true;
            this.possiblyByLoopBreaks = false;
        }
    }
    
    public SpecificationVisitor(Declaration declaration) {
        this.declaration = declaration;
    }
    
    private void declare() {
        declared = true;
    }
    
    private boolean beginDeclarationScope() {
        return declared;
    }
    
    private void endDeclarationScope(boolean d) {
        declared = d;
    }
    
    private boolean beginDisabledSpecificationScope() {
        boolean ca = specificationDisabled;
        specificationDisabled = true;
        return ca;
    }
    
    private void endDisabledSpecificationScope(boolean ca) {
        specificationDisabled = ca;
    }
    
    private void specify() {
        specified.definitely = true;
        specified.possibly = true;
    }
    
    private void exit() {
        specified.possiblyExited = true;
        specified.definitelyExited = true;
    }
    
    private SpecificationState beginSpecificationScope() {
        SpecificationState as = specified;
        specified = 
                new SpecificationState(specified.definitely, 
                        specified.possibly);
        return as;
    }
    
    private void endSpecificationScope(SpecificationState as) {
        specified = as;
    }
    
    private boolean isVariable() {
        if (declaration instanceof TypedDeclaration) {
            TypedDeclaration td = 
                    (TypedDeclaration) declaration;
            return td.isVariable();
        }
        else {
            return false;
        }
    }
    
    private boolean isLate() {
        if (declaration instanceof FunctionOrValue) {
            FunctionOrValue fov = 
                    (FunctionOrValue) declaration;
            return fov.isLate();
        }
        else {
            return false;
        }
    }
    
    @Override
    public void visit(Tree.AnnotationList that) {}
    
    @Override
    public void visit(Tree.BaseMemberExpression that) {
        super.visit(that);
        visitReference(that);
    }

    @Override
    public void visit(Tree.MetaLiteral that) {
        super.visit(that);
        visitReference(that);
    }

    @Override
    public void visit(Tree.ExtendedTypeExpression that) {
        super.visit(that);
        visitReference(that);
    }

    @Override
    public void visit(Tree.CaseTypes that) {
        //the BaseMemberExpressions in the CaseTypes
        //list are actually types, not value refs!
    }
    
    @Override
    public void visit(Tree.SatisfiedTypes that) {
        //unnecessary ... for consistency nothing else
    }
    
    @Override
    public void visit(Tree.BaseTypeExpression that) {
        super.visit(that);
        visitReference(that);
    }

    @Override
    public void visit(Tree.QualifiedMemberExpression that) {
        super.visit(that);
        if (isSelfReference(that.getPrimary())) {
            visitReference(that);
        }
    }

    @Override
    public void visit(Tree.QualifiedTypeExpression that) {
        super.visit(that);
        if (isSelfReference(that.getPrimary())) {
            visitReference(that);
        }
    }

    private void visitReference(Tree.Primary that) {
        Declaration member;
        boolean assigned;
        boolean metamodel;
        if (that instanceof Tree.MemberOrTypeExpression) {
            Tree.MemberOrTypeExpression mte = 
                    (Tree.MemberOrTypeExpression) that;
            member = mte.getDeclaration();
            assigned = mte.getAssigned();
            metamodel = false;
        }
        else if (that instanceof Tree.MetaLiteral) {
            Tree.MetaLiteral ml = (Tree.MetaLiteral) that;
            member = ml.getDeclaration();
            assigned = false;
            metamodel = true;
        }
        else {
            return;
        }

        Scope scope = that.getScope();
        if (member==declaration && 
                declaration.isDefinedInScope(scope) &&
                //TODO: THIS IS TERRIBLE!!!!!
                !isReferenceToNativeHeaderMember(scope)) {
            if (!declared) {
                //you are allowed to refer to later 
                //declarations in a class declaration
                //section or interface
                if (!metamodel && 
                        !isForwardReferenceable() && 
                        !hasParameter) {
                    Scope container = 
                            declaration.getContainer();
                    if (container instanceof Class) {
                        that.addError("forward reference to class member in initializer: '" + 
                                declaration.getName() + 
                                "' is not yet declared (forward references must occur in declaration section)");
                    }
                    else {
                        that.addError("forward reference to local declaration: '" + 
                                declaration.getName() + 
                                "' is not yet declared");
                    }
                }
            }
            else if (!specified.definitely || 
                    declaration.isFormal()) {
                //you are allowed to refer to formal
                //declarations in a class declaration
                //section or interface
                if (declaration.isFormal()) {
                    if (!isForwardReferenceable()) {
                        that.addError("formal member may not be used in initializer: '" + 
                                member.getName() + "'");                    
                    }
                }
                else if (!metamodel &&
                        !isNativeHeader(declaration) &&
                        (!isLate() || !isForwardReferenceable())) {
                    if (isVariable()) {
                        that.addError("not definitely initialized: '" + 
                                declaration.getName() + "'");                    
                    }
                    else {
                        that.addError("not definitely specified: '" + 
                                declaration.getName() + "'");
                    }
                }
            }
            else if (parameter!=null && 
                    isConstructor(parameter.getDeclaration())) {
                if (parameter.getDeclaration().getContainer()
                        .equals(declaration.getContainer())) {
                    that.addError("default argument to constructor parameter is a member of the constructed class");
                }
            }
            if (!assigned && declaration.isDefault() && 
                    !isForwardReferenceable()) {
                that.addError("default member may not be used in initializer: '" + 
                        declaration.getName() + "'"); 
            }
            if (inAnonFunctionOrComprehension && 
                specified.definitely && 
                isVariable()) {
                that.addError("variable member may not be captured by comprehension or function in extends clause: '"+
                        declaration.getName() + "'");
            }
        }
    }
    
    private boolean isReferenceToNativeHeaderMember(Scope scope) {
        if (declaration.isClassOrInterfaceMember()) {
            ClassOrInterface container = 
                    (ClassOrInterface) 
                        declaration.getContainer();
            return container.isNativeHeader() && 
                    !scope.getScopedBackends().none();
        }
        else {
            return false;
        }
    }

    private boolean isForwardReferenceable() {
        return declarationSection ||
                declaration.isToplevel() ||
                declaration.isInterfaceMember();
    }
    
    @Override
    public void visit(Tree.LogicalOp that) {
        that.getLeftTerm().visit(this);
        SpecificationState ss = beginSpecificationScope();
        that.getRightTerm().visit(this);
        endSpecificationScope(ss);
    }
    
    @Override
    public void visit(Tree.IfExpression that) {
        //TODO: reproduce the logic for IfStatement!
        Tree.IfClause ifClause = that.getIfClause();
        if (ifClause!=null) {
            SpecificationState ss = 
                    beginSpecificationScope();
            ifClause.visit(this);
            endSpecificationScope(ss);
        }
        Tree.ElseClause elseClause = that.getElseClause();
        if (elseClause!=null) {
            SpecificationState ss = 
                    beginSpecificationScope();
            elseClause.visit(this);
            endSpecificationScope(ss);
        }
    }
    
    @Override
    public void visit(Tree.SwitchExpression that) {
      //TODO: reproduce the logic for SwitchStatement!
        Tree.SwitchClause switchClause = 
                that.getSwitchClause();
        if (switchClause!=null) {
            switchClause.visit(this);
        }
        Tree.SwitchCaseList switchCaseList = 
                that.getSwitchCaseList();
        if (switchCaseList!=null) {
            for (Tree.CaseClause caseClause: 
                    switchCaseList.getCaseClauses()) {
                SpecificationState ss = 
                        beginSpecificationScope();
                caseClause.visit(this);
                endSpecificationScope(ss);
            }
            Tree.ElseClause elseClause = 
                    switchCaseList.getElseClause();
            if (elseClause!=null) {
                SpecificationState ss = 
                        beginSpecificationScope();
                elseClause.visit(this);
                endSpecificationScope(ss);
            }
        }
    }
    
    @Override
    public void visit(Tree.Comprehension that) {
        boolean oicoaf = inAnonFunctionOrComprehension;
        inAnonFunctionOrComprehension = declared&&inExtends;
        super.visit(that);
        inAnonFunctionOrComprehension = oicoaf;
    }
    
    @Override
    public void visit(Tree.FunctionArgument that) {
        boolean c = beginDisabledSpecificationScope();
        boolean oicoaf = inAnonFunctionOrComprehension;
        inAnonFunctionOrComprehension = declared&&inExtends;
        SpecificationState ss = beginSpecificationScope();
        super.visit(that);
        endSpecificationScope(ss);
        inAnonFunctionOrComprehension = oicoaf;
        endDisabledSpecificationScope(c);
    }
    
    @Override
    public void visit(Tree.ObjectExpression that) {
        boolean c = beginDisabledSpecificationScope();
        boolean oicoaf = inAnonFunctionOrComprehension;
        inAnonFunctionOrComprehension = declared&&inExtends;
        SpecificationState ss = beginSpecificationScope();
        super.visit(that);
        endSpecificationScope(ss);
        inAnonFunctionOrComprehension = oicoaf;
        endDisabledSpecificationScope(c);
    }
    
    @Override
    public void visit(Tree.AssignOp that) {
        Tree.Term lt = that.getLeftTerm();
        if (isEffectivelyBaseMemberExpression(lt)) {
            Tree.StaticMemberOrTypeExpression m = 
                    (Tree.StaticMemberOrTypeExpression) lt;
            Declaration member = m.getDeclaration();
            if (member==declaration) {
                if (that.getRightTerm()!=null) {
                    that.getRightTerm().visit(this);
                }
                checkVariable(lt, that);
                specify();
                lt.visit(this);
            }
            else {
                super.visit(that);
            }
        }
    }
    
    @Override
    public void visit(Tree.AssignmentOp that) {
        super.visit(that);
        checkVariable(that.getLeftTerm(), that);
    }

    @Override
    public void visit(Tree.PostfixOperatorExpression that) {
        super.visit(that);
        checkVariable(that.getTerm(), that);
    }
    
    @Override
    public void visit(Tree.PrefixOperatorExpression that) {
        super.visit(that);
        checkVariable(that.getTerm(), that);
    }
    
    private void checkVariable(Tree.Term term, Node node) {
        if (isEffectivelyBaseMemberExpression(term)) {  //Note: other cases handled in ExpressionVisitor
            Tree.StaticMemberOrTypeExpression mte = 
                    (Tree.StaticMemberOrTypeExpression) term;
            Declaration member = mte.getDeclaration();
            if (member==declaration) {
                if ((declaration.isFormal() || 
                     declaration.isDefault()) && 
                         !isForwardReferenceable()) {
                    term.addError("member is formal or default and may not be assigned here: '" +
                            member.getName() + "'");
                }
                else if (!isVariable() && !isLate()) {
                    if (member instanceof Value) {
                        if (node instanceof Tree.AssignOp) {
                            term.addError("value is not a variable and may not be assigned here: '" +
                                    member.getName() + "'", 
                                    803);
                        }
                        else {
                            term.addError("value is not a variable: '" +
                                    member.getName() + "'", 
                                    800);
                        }
                    }
                    else {
                        term.addError("not a variable value: '" +
                                member.getName() + "'");
                    }
                }
            }
        }
    }

    private Tree.Continue lastContinue;
    private Tree.Statement lastContinueStatement;
    
    @Override
    public void visit(Tree.Block that) {
        Scope scope = that.getScope();
        if (scope instanceof Constructor) {
            if (definitelyInitedBy.contains(delegatedConstructor)) {
                specified.definitely = true;
            }
            if (possiblyInitedBy.contains(delegatedConstructor)) {
                specified.possibly = true;
            }
            delegatedConstructor = null;
        }
        
        boolean of = endsInBreak;
        boolean oe = endsInReturnThrow;
        Tree.Continue olc = lastContinue;
        Tree.Statement olcs = lastContinueStatement;
        //rather nasty way of detecting that the continue
        //occurs in another conditional branch of the
        //statement containing this block, even though we
        //did not find it in _this_ branch
        boolean continueInSomeBranchOfCurrentConditional = 
                lastContinue!=null && 
                lastContinueStatement==null;
        boolean blockEndsInReturnThrow = 
                blockEndsInReturnThrow(that);
        boolean blockEndsInBreak = 
                !blockEndsInReturnThrow &&
                blockEndsInReturnThrowBreak(that);
        endsInBreak = endsInBreak || 
                blockEndsInBreak;
        endsInReturnThrow = endsInReturnThrow || 
                blockEndsInReturnThrow;
        Tree.Continue last = null;
        Tree.Statement lastStatement = null;
        for (Tree.Statement st: that.getStatements()) {
            ContinueVisitor cv = new ContinueVisitor(olc);
            st.visit(cv);
            if (cv.node!=null) {
                last = cv.node;
                lastStatement = st;
            }
            if (cv.found) {
                olc = null;
                olcs = null;
            }
        }
        if (blockEndsInReturnThrow || blockEndsInBreak ||
                continueInSomeBranchOfCurrentConditional) {
            lastContinue = last;
            lastContinueStatement = lastStatement;
        }
        super.visit(that);
        endsInBreak = of;
        endsInReturnThrow = oe;
        lastContinue = olc;
        lastContinueStatement = olcs;
        
        if (scope instanceof Constructor) {
            Constructor c = (Constructor) scope;
            if (specified.definitely) {
                definitelyInitedBy.add(c);
            }
            if (specified.possibly) {
                possiblyInitedBy.add(c);
            }
        }
        if (isNonPartialConstructor(scope) &&
                declaration.getContainer()==scope.getContainer()) {
            if (!specified.definitely) {
                initedByEveryConstructor = false;
            }
        }
    }
    
    @Override 
    public void visit(Tree.DelegatedConstructor that) {
        super.visit(that);
        Tree.SimpleType type = that.getType();
        if (type!=null) {
            delegatedConstructor = 
                    type.getDeclarationModel();
            if (delegatedConstructor instanceof Class) {
                //this case is not actually legal
                Class c = (Class) delegatedConstructor;
                delegatedConstructor = 
                        c.getDefaultConstructor();
            }
        }
    }
    
    private TypeDeclaration delegatedConstructor;
    
    private List<Constructor> definitelyInitedBy = 
            new ArrayList<Constructor>();
    private List<Constructor> possiblyInitedBy = 
            new ArrayList<Constructor>();
    
    private boolean initedByEveryConstructor = true;

    private boolean blockEndsInBreak(Tree.Block that) {
        if (that==null) {
            return false;
        }
        int size = that.getStatements().size();
        if (size>0) {
            Tree.Statement s = 
                    that.getStatements()
                        .get(size-1);
            if (s instanceof Tree.IfStatement) {
                Tree.IfStatement is = (Tree.IfStatement) s;
                Tree.IfClause ic = is.getIfClause();
                Tree.ElseClause ec = is.getElseClause();
                if (ic!=null) {
                    Tree.ConditionList cl = 
                            ic.getConditionList();
                    if (cl!=null) {
                        if (isAlwaysSatisfied(cl)) {
                            return blockEndsInBreak(
                                    ic.getBlock());
                        }
                        else if (ec!=null && isNeverSatisfied(cl)) {
                            return blockEndsInBreak(
                                    ec.getBlock());
                        }
                    }
                    if (ec!=null) {
                        return blockEndsInBreak(ic.getBlock())
                                && blockEndsInBreak(ec.getBlock());
                    }
                }
            }
            else if (s instanceof Tree.SwitchStatement) {
                Tree.SwitchStatement ss = (Tree.SwitchStatement) s;
                Tree.SwitchCaseList scl = ss.getSwitchCaseList();
                for (Tree.CaseClause cc: scl.getCaseClauses()){
                    if (!blockEndsInBreak(cc.getBlock())) {
                        return false;
                    }
                }
                Tree.ElseClause ec = scl.getElseClause();
                if (ec!=null) {
                    return blockEndsInBreak(ec.getBlock());
                }
            }
            else if (s instanceof Tree.ForStatement) {
                Tree.ForStatement fs = (Tree.ForStatement) s;
                Tree.ForClause fc = fs.getForClause();
                Tree.ElseClause ec = fs.getElseClause();
                if (fc!=null) {
                    if (isAtLeastOne(fc)) {
                        return blockEndsInBreak(fc.getBlock());
                    }
                    if (ec!=null) {
                        return blockEndsInBreak(fc.getBlock())
                                && blockEndsInBreak(ec.getBlock());
                    }
                }
            }
            return s instanceof Tree.Break;
        }
        else {
            return false;
        }
    }
    
    private boolean blockEndsInReturnThrow(Tree.Block that) {
        if (that==null) {
            return false;
        }
        int size = that.getStatements().size();
        if (size>0) {
            Tree.Statement s = 
                    that.getStatements()
                        .get(size-1);
            if (s instanceof Tree.IfStatement) {
                Tree.IfStatement is = (Tree.IfStatement) s;
                Tree.IfClause ic = is.getIfClause();
                Tree.ElseClause ec = is.getElseClause();
                if (ic!=null) {
                    Tree.ConditionList cl = 
                            ic.getConditionList();
                    if (cl!=null) {
                        if (isAlwaysSatisfied(cl)) {
                            return blockEndsInReturnThrow(
                                    ic.getBlock());
                        }
                        else if (ec!=null && isNeverSatisfied(cl)) {
                            return blockEndsInReturnThrow(
                                    ec.getBlock());
                        }
                    }
                    if (ec!=null) {
                        return blockEndsInReturnThrow(ic.getBlock())
                                && blockEndsInReturnThrow(ec.getBlock());
                    }
                }
            }
            else if (s instanceof Tree.SwitchStatement) {
                Tree.SwitchStatement ss = (Tree.SwitchStatement) s;
                Tree.SwitchCaseList scl = ss.getSwitchCaseList();
                for (Tree.CaseClause cc: scl.getCaseClauses()){
                    if (!blockEndsInReturnThrow(cc.getBlock())) {
                        return false;
                    }
                }
                Tree.ElseClause ec = scl.getElseClause();
                if (ec!=null) {
                    return blockEndsInReturnThrow(ec.getBlock());
                }
            }
            else if (s instanceof Tree.ForStatement) {
                Tree.ForStatement fs = (Tree.ForStatement) s;
                Tree.ForClause fc = fs.getForClause();
                Tree.ElseClause ec = fs.getElseClause();
                if (fc!=null) {
                    if (isAtLeastOne(fc)) {
                        return blockEndsInReturnThrow(fc.getBlock());
                    }
                    if (ec!=null) {
                        return blockEndsInReturnThrow(fc.getBlock())
                                && blockEndsInReturnThrow(ec.getBlock());
                    }
                }
            }
            return s instanceof Tree.Return ||
                    s instanceof Tree.Throw;
        }
        else {
            return false;
        }
    }
    
    private boolean blockEndsInReturnThrowBreak(Tree.Block that) {
        if (that==null) {
            return false;
        }
        int size = that.getStatements().size();
        if (size>0) {
            Tree.Statement s = 
                    that.getStatements()
                        .get(size-1);
            if (s instanceof Tree.IfStatement) {
                Tree.IfStatement is = (Tree.IfStatement) s;
                Tree.IfClause ic = is.getIfClause();
                Tree.ElseClause ec = is.getElseClause();
                if (ic!=null) {
                    Tree.ConditionList cl = 
                            ic.getConditionList();
                    if (cl!=null) {
                        if (isAlwaysSatisfied(cl)) {
                            return blockEndsInReturnThrowBreak(
                                    ic.getBlock());
                        }
                        else if (ec!=null && isNeverSatisfied(cl)) {
                            return blockEndsInReturnThrowBreak(
                                    ec.getBlock());
                        }
                    }
                    if (ec!=null) {
                        return blockEndsInReturnThrowBreak(ic.getBlock())
                                && blockEndsInReturnThrowBreak(ec.getBlock());
                    }
                }
            }
            else if (s instanceof Tree.SwitchStatement) {
                Tree.SwitchStatement ss = (Tree.SwitchStatement) s;
                Tree.SwitchCaseList scl = ss.getSwitchCaseList();
                for (Tree.CaseClause cc: scl.getCaseClauses()){
                    if (!blockEndsInReturnThrowBreak(cc.getBlock())) {
                        return false;
                    }
                }
                Tree.ElseClause ec = scl.getElseClause();
                if (ec!=null) {
                    return blockEndsInReturnThrowBreak(ec.getBlock());
                }
            }
            else if (s instanceof Tree.ForStatement) {
                Tree.ForStatement fs = (Tree.ForStatement) s;
                Tree.ForClause fc = fs.getForClause();
                Tree.ElseClause ec = fs.getElseClause();
                if (fc!=null) {
                    if (isAtLeastOne(fc)) {
                        return blockEndsInReturnThrowBreak(fc.getBlock());
                    }
                    if (ec!=null) {
                        return blockEndsInReturnThrowBreak(fc.getBlock())
                                && blockEndsInReturnThrowBreak(ec.getBlock());
                    }
                }
            }
            return s instanceof Tree.Return ||
                    s instanceof Tree.Throw ||
                    s instanceof Tree.Break;
        }
        else {
            return false;
        }
    }
    
    @Override
    public void visit(Tree.ForClause that) {
        boolean of = endsInBreak;
        boolean oe = endsInReturnThrow;
        Tree.Continue olc = lastContinue;
        lastContinue = null;
        endsInBreak = false;
        endsInReturnThrow = false;
        super.visit(that);
        endsInBreak = of;
        endsInReturnThrow = oe;
        lastContinue = olc;
    }
    
    @Override
    public void visit(Tree.WhileClause that) {
        boolean of = endsInBreak;
        boolean oe = endsInReturnThrow;
        Tree.Continue olc = lastContinue;
        lastContinue = null;
        endsInBreak = false;
        endsInReturnThrow = false;
        super.visit(that);
        endsInBreak = of;
        endsInReturnThrow = oe;
        lastContinue = olc;
    }
    
    @Override
    public void visit(Tree.Body that) {
        if (hasParameter &&
                that.getScope()==declaration.getContainer()) {
            hasParameter = false;
        }
        super.visit(that);
    }

    private static boolean isNonPartialConstructor(Scope scope) {
        if (scope instanceof Constructor) {
            Constructor constructor = (Constructor) scope;
            return !constructor.isAbstract();
        }
        else {
            return false;
        }
    }
    
    private String longdesc() {
        if (declaration instanceof Value) {
            return "value is neither variable nor late and";
        }
        else if (declaration instanceof Function) {
            return "function";
        }
        else {
            return "declaration";
        }
    }
    
    private String shortdesc() {
        if (declaration instanceof Value) {
            return "value";
        }
        else if (declaration instanceof Function) {
            return "function";
        }
        else {
            return "declaration";
        }
    }
    
    @Override
    public void visit(Tree.SpecifierStatement that) {
        Tree.Term term = that.getBaseMemberExpression();
        boolean parameterized = false;
        while (term instanceof Tree.ParameterizedExpression) {
            Tree.ParameterizedExpression pe = 
                    (Tree.ParameterizedExpression) term;
            term = pe.getPrimary();
            parameterized = true;
        }
        if (term instanceof Tree.StaticMemberOrTypeExpression) {
            Tree.StaticMemberOrTypeExpression bme = 
                    (Tree.StaticMemberOrTypeExpression) 
                        term;
            Declaration member = bme.getDeclaration();
            if (member==declaration) {
                if (!isForwardReferenceable()) {
                    if (declaration.isFormal()) {
                        bme.addError("member is formal and may not be specified: '" +
                                declaration.getName() + "' is declared formal");
                    }
                    else if (declaration.isDefault()) {
                        bme.addError("member is default and may not be specified except in its declaration: '" +
                                declaration.getName() + "' is declared default");
                    }
                }
                if (that.getRefinement()) {
                    declare();
                }
                Tree.SpecifierExpression se = 
                        that.getSpecifierExpression();
                boolean lazy = se instanceof 
                        Tree.LazySpecifierExpression;
                checkSpecifiedValue(se);
                if (!lazy || !parameterized) {
                    se.visit(this);
                }
                
                if (that.getRefinement()) {
                    specify();
                    term.visit(this);
                }
                else {
                    specification(that, bme);
                }
                if (lazy && parameterized) {
                    se.visit(this);
                }
                checkDeclarationSection(that);
            }
            else {
                super.visit(that);
            }
        }
        else {
            super.visit(that);
        }
    }
    
    private void checkSpecifiedValue(Tree.SpecifierExpression se) {
        if (declaration instanceof Value) {
            boolean lazy = se instanceof 
                    Tree.LazySpecifierExpression;
            Value value = (Value) declaration;
            if (!value.isVariable() &&
                    lazy!=value.isTransient()) {
                // check that all assignments to a non-variable, in
                // different paths of execution, all use the same
                // kind of specifier, all =>, or all =
                // TODO: sometimes this error appears only because 
                //       of a later line which illegally reassigns
                se.addError("value must be specified using => lazy specifier: '" +
                        declaration.getName() + "'");
            }
            if (lazy) {
                if (value.isVariable()) {
                    se.addError("variable value may not be specified using => lazy specifier: '" +
                            declaration.getName() + "'");
                }
                else if (value.isLate()) {
                    se.addError("late reference may not be specified using => lazy specifier: '" +
                            declaration.getName() + "'");
                }
            }
        }
    }
    
    private void specification(Tree.SpecifierStatement that, 
            Tree.StaticMemberOrTypeExpression bme) {
        boolean constant = !isVariable() && !isLate();
        Scope scope = that.getScope();
        if (constant && 
                (!declaration.isDefinedInScope(scope) ||
                declaration instanceof FunctionOrValue &&
                ((FunctionOrValue) declaration).isShortcutRefinement())) {
            //this error is added by ExpressionVisitor
//          that.addError("inherited member is not variable and may not be specified here: '" + 
//                  member.getName() + "'");
        }
        else if (!declared && constant) {
            bme.addError(shortdesc() + 
                    " is not yet declared: '" + 
                    declaration.getName() + "'");
        }
        else if (loopDepth>0 && constant  && 
                !(endsInReturnThrow && 
                        lastContinue==null ||
                  endsInBreak && allOuterLoopsBreak &&
                          lastContinue==null)) {
            if (specified.definitely) {
                bme.addError(longdesc() + 
                        " is aready definitely specified: '" + 
                        declaration.getName() + "'", 
                        803);
            }
            else {
                bme.addError(longdesc() + 
                        " is not definitely unspecified in loop: '" + 
                        declaration.getName() + "'", 
                        803);
                specify(); //to eliminate dupe error
            }
        }
        else if (specificationDisabled && constant) {
            if (withinDeclaration) {
                bme.addError("cannot specify " + 
                        shortdesc() + 
                        " from within its own body: '" + 
                        declaration.getName() + "'");
            }
            else {
                bme.addError("cannot specify " + 
                        shortdesc() + 
                        " declared in outer scope: '" + 
                        declaration.getName() + "'", 
                        803);
            }
        }
        else if (specified.possibly && constant) {
            if (specified.definitely) {
                bme.addError(longdesc() + 
                        " is aready definitely specified: '" + 
                        declaration.getName() + "'", 
                        803);
            }
            else {
                bme.addError(longdesc() + 
                        " is not definitely unspecified: '" + 
                        declaration.getName() + "'", 
                        803);
                specify(); //to eliminate dupe error
            }
        }
        else {
            specify();
            bme.visit(this);
        }
    }
    
    @Override
    public void visit(Tree.Declaration that) {
        boolean oe = endsInReturnThrow;
        boolean of = endsInBreak;
        Tree.Continue olc = lastContinue;
        lastContinue = null;
        endsInReturnThrow = false;
        endsInBreak = false;
        if (that.getDeclarationModel()==declaration) {
            loopDepth = 0;
            brokenLoopDepth = 0;
            beginDisabledSpecificationScope();
            withinDeclaration = true;
            declare();
            super.visit(that);
            withinDeclaration = false;
            endDisabledSpecificationScope(false);
            loopDepth = 0;
            brokenLoopDepth = 0;
        }
        else {
            int l = loopDepth;
            int bl = brokenLoopDepth;
            loopDepth = 0;
            brokenLoopDepth = 0;
            Scope scope = that.getScope();
            boolean constructor = 
                    scope instanceof Constructor;
            boolean valueWithInitializer =
                    scope instanceof Value &&
                    !((Value) scope).isTransient();
            boolean c = false;
            if (!constructor) {
                c = beginDisabledSpecificationScope();
            }
            boolean d = beginDeclarationScope();
            SpecificationState as; 
            if (!valueWithInitializer) {
                as = beginSpecificationScope();
            }
            else {
                as = null;
            }
            super.visit(that);
            if (!constructor) {
                endDisabledSpecificationScope(c);
            }
            endDeclarationScope(d);
            if (!valueWithInitializer) {
                endSpecificationScope(as);
            }
            loopDepth = l;
            brokenLoopDepth = bl;
        }
        endsInReturnThrow = oe;
        endsInBreak = of;
        lastContinue = olc;
    }
    
    @Override
    public void visit(Tree.Constructor that) {
        Function f = that.getDeclarationModel();
        Constructor c = that.getConstructor();
        if (f==declaration || c==declaration) {
            declare();
            specify();
        }
        super.visit(that);
        if (declaration.getContainer()==c.getContainer() &&
                that==lastConstructor && 
                initedByEveryConstructor) {
            specified.definitely = true;
        }
    }

    @Override
    public void visit(Tree.Enumerated that) {
        Value v = that.getDeclarationModel();
        Constructor e = that.getEnumerated();
        if (v==declaration || e==declaration) {
            declare();
            specify();
        }
        super.visit(that);
        if (declaration.getContainer()==e.getContainer() &&
                that==lastConstructor && 
                initedByEveryConstructor) {
            specified.definitely = true;
        }
    }

    @Override
    public void visit(Tree.TypedArgument that) {
        if (that.getDeclarationModel()==declaration) {
            loopDepth = 0;
            brokenLoopDepth = 0;
            beginDisabledSpecificationScope();
            withinDeclaration = true;
            super.visit(that);
            declare();
            withinDeclaration = false;
            endDisabledSpecificationScope(false);
            loopDepth = 0;
            brokenLoopDepth = 0;
        }
        else {
            int l = loopDepth;
            int bl = brokenLoopDepth;
            loopDepth = 0;
            brokenLoopDepth = 0;
            boolean c = beginDisabledSpecificationScope();
            boolean d = beginDeclarationScope();
            SpecificationState as = 
                    beginSpecificationScope();
            super.visit(that);
            endDisabledSpecificationScope(c);
            endDeclarationScope(d);
            endSpecificationScope(as);
            loopDepth = l;
            brokenLoopDepth = bl;
        }
    }
    
    @Override
    public void visit(Tree.MethodDeclaration that) {
        if (that.getDeclarationModel()==declaration) {
            if (that.getSpecifierExpression()!=null) {
                specify();
                super.visit(that);
            }
            else {
                super.visit(that);
                if (declaration.isToplevel() && 
                        !isNativeHeader(declaration)) {
                    that.addError("toplevel function must be specified: '" +
                            declaration.getName() + 
                            "' may not be forward declared");
                }
                else if (declaration.isClassMember() && 
                        !isNativeHeader(declaration) &&
                        !declaration.isFormal() && 
                        that.getDeclarationModel()
                            .getInitializerParameter()==null &&
                        declarationSection) {
                    that.addError("forward declaration may not occur in declaration section: '" +
                                declaration.getName() + "'", 
                                1450);
                }
                else if (declaration.isInterfaceMember() && 
                        !isNativeHeader(declaration) &&
                        !declaration.isFormal()) {
                    that.addError("interface method must be formal or specified: '" +
                            declaration.getName() + "'", 
                            1400);
                }
            }
        }
        else {
            super.visit(that);
        }
    }
    
    @Override
    public void visit(Tree.MethodDefinition that) {
        if (that.getDeclarationModel()==declaration) {
            declare();
            specify();
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.MethodArgument that) {
        if (that.getDeclarationModel()==declaration) {
            declare();
            specify();
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.Variable that) {
        super.visit(that);
        if (that.getDeclarationModel()==declaration) {
            specify();
        }
    }
    
    @Override
    public void visit(Tree.Parameter that) {
        Parameter oip = parameter;
        parameter = that.getParameterModel();
        super.visit(that);
        parameter = oip;
        if (that.getParameterModel().getModel()==declaration) {
            specify();
        }
    }
    
    @Override
    public void visit(Tree.InitializerParameter that) {
        super.visit(that);
        Parameter d = that.getParameterModel();
        Declaration a = 
                that.getScope()
                    .getDirectMember(d.getName(), 
                            null, false);
        if (a!=null && a==declaration) {
            specify();
            hasParameter = true;
        }
    }
    
    @Override
    public void visit(Tree.TypeParameterDeclaration that) {
        super.visit(that);
        if (that.getDeclarationModel()==declaration) {
            specify();
        }
    }
    
    @Override
    public void visit(Tree.AttributeDeclaration that) {
        if (that.getDeclarationModel()==declaration) {
            Tree.SpecifierOrInitializerExpression sie = 
                    that.getSpecifierOrInitializerExpression();
            if (sie!=null) {
                super.visit(that);
                specify();
            }
            else {
                super.visit(that);
                if (declaration.isToplevel() && 
                        !isNativeHeader(declaration) &&
                        !isLate()) {
                    if (isVariable()) {
                        that.addError("toplevel variable value must be initialized: '" +
                                declaration.getName() + "'");
                    }
                    else {
                        that.addError("toplevel value must be specified: '" +
                                declaration.getName() + "'");
                    }
                }
                else if (declaration.isClassOrInterfaceMember() && 
                        !isNativeHeader(declaration) &&
                        !declaration.isFormal() &&
                        that.getDeclarationModel()
                            .getInitializerParameter()==null &&
                        !that.getDeclarationModel().isLate() &&
                        declarationSection) {
                    that.addError("forward declaration may not occur in declaration section: '" +
                            declaration.getName() + "'", 
                            1450);
                }
            }
        }
        else {
            super.visit(that);
        }
    }
    
    @Override
    public void visit(Tree.AttributeGetterDefinition that) {
        if (that.getDeclarationModel()==declaration) {
            declare();
            super.visit(that);        
            specify();
        }
        else {
            super.visit(that);        
        }
    }
    
    @Override
    public void visit(Tree.AttributeSetterDefinition that) {
        Setter d = that.getDeclarationModel();
        if (d==declaration ||
            d.getParameter().getModel()==declaration) {
            declare();
            specify();
        }
        super.visit(that);        
    }
    
    @Override
    public void visit(Tree.AttributeArgument that) {
        if (that.getDeclarationModel()==declaration) {
            declare();
            specify();
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.ObjectDefinition that) {
        if (that.getDeclarationModel()==declaration) {
            declare();
            specify();
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.ObjectArgument that) {
        if (that.getDeclarationModel()==declaration) {
            declare();
            specify();
        }
        super.visit(that);
    }
    
    private Tree.Declaration getDeclaration(Tree.ClassBody that) {
        for (Tree.Statement s: that.getStatements()) {
            if (s instanceof Tree.Declaration) {
                Tree.Declaration d = (Tree.Declaration) s;
                if (d.getDeclarationModel()==declaration) {
                    return d;
                }
            }
        }
        return null;
    }
        
    @Override
    public void visit(Tree.ClassBody that) {
        if (that.getScope()==declaration.getContainer()) {
            Tree.Statement les = getLastExecutableStatement(that);
            Tree.Declaration lc = getLastConstructor(that);
            declarationSection = les==null;
            lastExecutableStatement = les;
            lastConstructor = lc;
            
            new Visitor() {
                boolean declarationSection = false;
                @Override
                public void visit(Tree.ExecutableStatement that) {
                    super.visit(that);
                    if (that==lastExecutableStatement) {
                        declarationSection = true;
                    }
                }
                @Override
                public void visit(Tree.Declaration that) {
                    super.visit(that);
                    if (declarationSection &&
                            that.getDeclarationModel()==declaration) {
                        definedInDeclarationSection = true;
                    }
                    if (that==lastExecutableStatement) {
                        declarationSection = true;
                    }
                }
                @Override
                public void visit(Tree.StaticMemberOrTypeExpression that) {
                    super.visit(that);
                    if (declarationSection &&
                            declaration instanceof FunctionOrValue &&
                            that.getDeclaration()==declaration) {
                        usedInDeclarationSection = true;;
                    }
                }
            }.visit(that);
            
            super.visit(that);        
            declarationSection = false;
            lastExecutableStatement = null;
            lastConstructor = null;

            if (!declaration.isAnonymous()) {
                if (isSharedDeclarationUninitialized()) {
                    Node d = getDeclaration(that);
                    if (d==null) d = that;
                    d.addError("must be definitely specified by class initializer: " + 
                                message(declaration) + 
                                (declaration.isShared() ? 
                                        " is shared" : 
                                        " is captured"), 
                                1401);
                }
            }
        }
        else {
            super.visit(that);
        }
    }
    
    @Override
    public void visit(Tree.Statement that) {
        super.visit(that);
        checkDeclarationSection(that);
    }

    private void checkDeclarationSection(Tree.Statement that) {
        declarationSection = 
                declarationSection || 
                that==lastExecutableStatement;
    }
    
    @Override
    public void visit(Tree.ClassOrInterface that) {
        if (that.getDeclarationModel()==declaration) {
            declare();
            specify();
        }
        super.visit(that);        
    }
    
    @Override
    public void visit(Tree.TypeAliasDeclaration that) {
        if (that.getDeclarationModel()==declaration) {
            declare();
            specify();
        }
        super.visit(that);        
    }
    
    public void visit(Tree.Return that) {
        super.visit(that);
        if (!specificationDisabled && 
                isSharedDeclarationUninitialized()) {
            that.addError("must be definitely specified by class initializer: " +
                    message(declaration) + 
                    (declaration.isShared() ? 
                            " is shared" : 
                            " is captured"));
        }
        else if (that.getDeclaration()==declaration.getContainer() &&
                isCapturedDeclarationUninitialized()) {
            that.addError("must be definitely specified by class initializer: " +
                    message(declaration) + 
                    (declaration.isShared() ? 
                            " is shared" : 
                            " is captured"));
        }
        exit();
    }

    private boolean isSharedDeclarationUninitialized() {
        return (declaration.isShared() || 
                declaration.getOtherInstanceAccess()) && 
                !declaration.isFormal() && 
                !isNativeHeader(declaration) &&
                !isLate() &&
                !specified.definitely;
    }
    
    private boolean isCapturedDeclarationUninitialized() {
        return (declaration.isShared() || 
                declaration.getOtherInstanceAccess() ||
                usedInDeclarationSection) &&
                !definedInDeclarationSection &&
                !declaration.isFormal() && 
                !isNativeHeader(declaration) &&
                !isLate() &&
                !specified.definitely;
    }
    
    @Override
    public void visit(Tree.Throw that) {
        super.visit(that);
        exit();
    }
    
    @Override
    public void visit(Tree.Assertion that) {
        super.visit(that);
        if (isNeverSatisfied(that.getConditionList())) {
            exit();
        }
    }
    
    @Override
    public void visit(Tree.Break that) {
        super.visit(that);
        exit();
        if (!specified.definitely) {
            specified.definitelyByLoopBreaks = false;
        }
        if (specified.possibly) {
            specified.possiblyByLoopBreaks = true;
        }
    }

    @Override
    public void visit(Tree.Continue that) {
        super.visit(that);
        exit();
        if (lastContinue==that) {
            lastContinue=null;
        }
    }
    
    @Override
    public void visit(Tree.IfStatement that) {        
        if (that==lastContinueStatement) {
            lastContinueStatement=null;
        }

        Tree.IfClause ifClause = that.getIfClause();
        Tree.ConditionList conditionList = 
                ifClause.getConditionList();
        if (ifClause!=null) {
            if (conditionList!=null) {
                conditionList.visit(this);
            }
        }
        
        boolean d = beginDeclarationScope();
        SpecificationState as = beginSpecificationScope();
        if (ifClause!=null) {
            Tree.Block block = ifClause.getBlock();
            if (block!=null) {
                block.visit(this);
            }
        }
        boolean definitelyAssignedByIfClause = 
                specified.definitely || 
                specified.definitelyExited;
        boolean possiblyAssignedByIfClause = 
                specified.possibly;
        boolean possiblyExitedFromIfClause = 
                specified.possiblyExited;
        boolean definitelyExitedFromIfClause = 
                specified.definitelyExited;
        boolean definitelySpecifiedByExitsFromIfClause = 
                specified.definitelyByLoopBreaks;
        boolean possiblySpecifiedByExitsFromIfClause = 
                specified.possiblyByLoopBreaks;
        endDeclarationScope(d);
        endSpecificationScope(as);
        
        boolean definitelyAssignedByElseClause;
        boolean possiblyAssignedByElseClause;
        boolean definitelyExitedFromElseClause;
        boolean possiblyExitedFromElseClause;
        boolean definitelySpecifiedByExitsFromElseClause;
        boolean possiblySpecifiedByExitsFromElseClause;
        Tree.ElseClause elseClause = that.getElseClause();
        if (elseClause!=null) {
            d = beginDeclarationScope();
            as = beginSpecificationScope();
            elseClause.visit(this);
            definitelyAssignedByElseClause = 
                    specified.definitely || 
                    specified.definitelyExited;
            possiblyAssignedByElseClause = 
                    specified.possibly;
            definitelyExitedFromElseClause =
                    specified.definitelyExited;
            possiblyExitedFromElseClause = 
                    specified.possiblyExited;
            definitelySpecifiedByExitsFromElseClause = 
                    specified.definitelyByLoopBreaks;
            possiblySpecifiedByExitsFromElseClause = 
                    specified.possiblyByLoopBreaks;
            endDeclarationScope(d);
            endSpecificationScope(as);
        }
        else {
            definitelyAssignedByElseClause = false;
            possiblyAssignedByElseClause = false;
            definitelyExitedFromElseClause = false;
            possiblyExitedFromElseClause = false;
            definitelySpecifiedByExitsFromElseClause = true;
            possiblySpecifiedByExitsFromElseClause = false;
        }
        
        if (isAlwaysSatisfied(conditionList)) {
            specified.definitely = specified.definitely || 
                    definitelyAssignedByIfClause;
            specified.possibly = specified.possibly || 
                    possiblyAssignedByIfClause && 
                    !definitelyExitedFromIfClause;
            specified.definitelyExited = specified.definitelyExited || 
                    definitelyExitedFromIfClause;
            specified.possiblyExited = specified.possiblyExited || 
                    possiblyExitedFromIfClause;
            specified.definitelyByLoopBreaks = specified.definitelyByLoopBreaks && 
                    definitelySpecifiedByExitsFromIfClause;
            specified.possiblyByLoopBreaks = specified.possiblyByLoopBreaks || 
                    possiblySpecifiedByExitsFromIfClause;
        } 
        else if (isNeverSatisfied(conditionList)) {
            specified.definitely = specified.definitely || 
                    definitelyAssignedByElseClause;
            specified.possibly = specified.possibly || 
                    possiblyAssignedByElseClause && 
                    !definitelyExitedFromElseClause;
            specified.definitelyExited = specified.definitelyExited || 
                    definitelyExitedFromElseClause;
            specified.possiblyExited = specified.possiblyExited || 
                    possiblyExitedFromElseClause;
            specified.definitelyByLoopBreaks = specified.definitelyByLoopBreaks && 
                    definitelySpecifiedByExitsFromElseClause;
            specified.possiblyByLoopBreaks = specified.possiblyByLoopBreaks || 
                    possiblySpecifiedByExitsFromElseClause;
        }
        else {
            specified.definitely = specified.definitely || 
                    definitelyAssignedByIfClause && 
                    definitelyAssignedByElseClause;
            specified.possibly = specified.possibly || 
                    possiblyAssignedByIfClause && 
                    !definitelyExitedFromIfClause || 
                    possiblyAssignedByElseClause && 
                    !definitelyExitedFromElseClause;
            specified.definitelyExited = specified.definitelyExited || 
                    definitelyExitedFromIfClause &&
                    definitelyExitedFromElseClause;
            specified.possiblyExited = specified.possiblyExited || 
                    possiblyExitedFromIfClause || 
                    possiblyExitedFromElseClause;
            specified.definitelyByLoopBreaks = specified.definitelyByLoopBreaks && 
                    definitelySpecifiedByExitsFromIfClause && 
                    definitelySpecifiedByExitsFromElseClause;
            specified.possiblyByLoopBreaks = specified.possiblyByLoopBreaks || 
                    possiblySpecifiedByExitsFromIfClause ||
                    possiblySpecifiedByExitsFromIfClause;
        }
        
        checkDeclarationSection(that);
    }
    
    @Override
    public void visit(Tree.TryCatchStatement that) {
        if (that==lastContinueStatement) {
            lastContinueStatement=null;
        }
        
        boolean d = beginDeclarationScope();
        SpecificationState as = beginSpecificationScope();
        Tree.TryClause tryClause = that.getTryClause();
        if (tryClause!=null ) {
            tryClause.visit(this);
        }
        boolean definitelyAssignedByTryClause = 
                specified.definitely || 
                specified.definitelyExited;
        boolean possiblyAssignedByTryClause = 
                specified.possibly;
        boolean possiblyExitedFromTryClause = 
                specified.possiblyExited;
        boolean definitelySpecifiedByExitsFromTryClause = 
                specified.definitelyByLoopBreaks;
        boolean possiblySpecifiedByExitsFromTryClause = 
                specified.possiblyByLoopBreaks;
        endDeclarationScope(d);
        endSpecificationScope(as);
        specified.possibly = specified.possibly || 
                possiblyAssignedByTryClause;
        specified.possiblyExited = specified.possiblyExited || 
                possiblyExitedFromTryClause;
        
        boolean definitelyAssignedByEveryCatchClause = true;
        boolean possiblyAssignedBySomeCatchClause = false;
        boolean definitelyExitedFromEveryCatchClause = true;
        boolean possiblyExitedFromSomeCatchClause = false;
        boolean specifiedByExitsFromEveryCatchClause = true;
        boolean specifiedByExitsFromSomeCatchClause = false;
        for (Tree.CatchClause cc: that.getCatchClauses()) {
            d = beginDeclarationScope();
            as = beginSpecificationScope();
            cc.visit(this);
            definitelyAssignedByEveryCatchClause = 
                    definitelyAssignedByEveryCatchClause && 
                    (specified.definitely || specified.definitelyExited);
            possiblyAssignedBySomeCatchClause = 
                    possiblyAssignedBySomeCatchClause || 
                    specified.possibly;
            definitelyExitedFromEveryCatchClause = 
                    definitelyExitedFromEveryCatchClause &&
                    specified.definitelyExited;
            possiblyExitedFromSomeCatchClause = 
                    possiblyExitedFromSomeCatchClause || 
                    specified.possiblyExited;
            specifiedByExitsFromEveryCatchClause = 
                    specifiedByExitsFromEveryCatchClause && 
                    specified.definitelyByLoopBreaks;
            specifiedByExitsFromSomeCatchClause =
                    specifiedByExitsFromSomeCatchClause ||
                    specified.possiblyByLoopBreaks;
            endDeclarationScope(d);
            endSpecificationScope(as);
        }
        specified.possibly = specified.possibly || 
                possiblyAssignedBySomeCatchClause;
        specified.possiblyExited = specified.possiblyExited || 
                possiblyExitedFromSomeCatchClause;
        
        boolean definitelyAssignedByFinallyClause;
        boolean possiblyAssignedByFinallyClause;
        boolean definitelyExitedFromFinallyClause;
        boolean possiblyExitedFromFinallyClause;
        boolean definitelySpecifiedByExitsFromFinallyClause;
        boolean possiblySpecifiedByExitsFromFinallyClause;
        Tree.FinallyClause finallyClause = 
                that.getFinallyClause();
        if (finallyClause!=null) {
            d = beginDeclarationScope();
            as = beginSpecificationScope();
            finallyClause.visit(this);
            definitelyAssignedByFinallyClause = 
                    specified.definitely || 
                    specified.definitelyExited;
            possiblyAssignedByFinallyClause = 
                    specified.possibly;
            definitelyExitedFromFinallyClause = 
                    specified.definitelyExited;
            possiblyExitedFromFinallyClause = 
                    specified.possiblyExited;
            definitelySpecifiedByExitsFromFinallyClause = 
                    specified.definitelyByLoopBreaks;
            possiblySpecifiedByExitsFromFinallyClause =
                    specified.possiblyByLoopBreaks;
            endDeclarationScope(d);
            endSpecificationScope(as);
        }
        else {
            definitelyAssignedByFinallyClause = false;
            possiblyAssignedByFinallyClause = false;
            definitelyExitedFromFinallyClause = false;
            possiblyExitedFromFinallyClause = false;
            definitelySpecifiedByExitsFromFinallyClause = true;
            possiblySpecifiedByExitsFromFinallyClause = false;
        }
        specified.possibly = specified.possibly || 
                possiblyAssignedByFinallyClause;
        specified.definitely = specified.definitely || 
                definitelyAssignedByFinallyClause || 
                definitelyAssignedByTryClause && 
                definitelyAssignedByEveryCatchClause;
        specified.definitelyExited = specified.definitelyExited &&
                definitelyExitedFromFinallyClause;
        specified.possiblyExited = specified.possiblyExited || 
                possiblyExitedFromFinallyClause;
        specified.definitelyByLoopBreaks = specified.definitelyByLoopBreaks || 
                definitelySpecifiedByExitsFromFinallyClause || 
                specifiedByExitsFromEveryCatchClause && 
                definitelySpecifiedByExitsFromTryClause;
        specified.possiblyByLoopBreaks = specified.possiblyByLoopBreaks ||
                possiblySpecifiedByExitsFromFinallyClause ||
                specifiedByExitsFromSomeCatchClause ||
                possiblySpecifiedByExitsFromTryClause;
        
        checkDeclarationSection(that);
    }
    
    @Override
    public void visit(Tree.SwitchStatement that) {
        if (that==lastContinueStatement) {
            lastContinueStatement=null;
        }
        
        Tree.SwitchClause switchClause = 
                that.getSwitchClause();
        if (switchClause!=null) {
            switchClause.visit(this);
        }
        boolean definitelyAssignedByEveryCaseClause = true;
        boolean possiblyAssignedBySomeCaseClause = false;
        boolean definitelyExitedFromEveryCaseClause = false;
        boolean possiblyExitedFromSomeCaseClause = false;
        boolean specifiedByExitsFromEveryCaseClause = true; 
        boolean specifiedByExitsFromSomeCaseClause = false; 
        boolean possiblyAssignedAndNotDefinitelyExitedFromSomeCaseClause = false; 
        
        Tree.SwitchCaseList switchCaseList = 
                that.getSwitchCaseList();
        for (Tree.CaseClause cc: 
                switchCaseList.getCaseClauses()) {
            boolean d = beginDeclarationScope();
            SpecificationState as = beginSpecificationScope();
            cc.visit(this);
            definitelyAssignedByEveryCaseClause = 
                    definitelyAssignedByEveryCaseClause && 
                    (specified.definitely || specified.definitelyExited);
            possiblyAssignedBySomeCaseClause = 
                    possiblyAssignedBySomeCaseClause || 
                    specified.possibly;
            definitelyExitedFromEveryCaseClause = 
                    definitelyExitedFromEveryCaseClause &&
                    specified.definitelyExited;
            possiblyExitedFromSomeCaseClause = 
                    possiblyExitedFromSomeCaseClause || 
                    specified.possiblyExited;
            specifiedByExitsFromEveryCaseClause = 
                    specifiedByExitsFromEveryCaseClause && 
                    specified.definitelyByLoopBreaks;
            specifiedByExitsFromSomeCaseClause = 
                    specifiedByExitsFromSomeCaseClause ||
                    specified.possiblyByLoopBreaks;
            possiblyAssignedAndNotDefinitelyExitedFromSomeCaseClause =
                    possiblyAssignedAndNotDefinitelyExitedFromSomeCaseClause ||
                    specified.possibly && !specified.definitelyExited;
            endDeclarationScope(d);
            endSpecificationScope(as);
        }
        
        Tree.ElseClause elseClause = 
                switchCaseList.getElseClause();
        if (elseClause!=null) {
            boolean d = beginDeclarationScope();
            SpecificationState as = beginSpecificationScope();
            elseClause.visit(this);
            definitelyAssignedByEveryCaseClause = 
                    definitelyAssignedByEveryCaseClause && 
                    (specified.definitely || specified.definitelyExited);
            possiblyAssignedBySomeCaseClause = 
                    possiblyAssignedBySomeCaseClause || 
                    specified.possibly;
            definitelyExitedFromEveryCaseClause = 
                    definitelyExitedFromEveryCaseClause &&
                    specified.definitelyExited;
            possiblyExitedFromSomeCaseClause = 
                    possiblyExitedFromSomeCaseClause || 
                    specified.possiblyExited;
            specifiedByExitsFromEveryCaseClause = 
                    specifiedByExitsFromEveryCaseClause && 
                    specified.definitelyByLoopBreaks;
            specifiedByExitsFromSomeCaseClause = 
                    specifiedByExitsFromSomeCaseClause ||
                    specified.possiblyByLoopBreaks;
            possiblyAssignedAndNotDefinitelyExitedFromSomeCaseClause =
                    possiblyAssignedAndNotDefinitelyExitedFromSomeCaseClause ||
                    specified.possibly && !specified.definitelyExited;
            endDeclarationScope(d);
            endSpecificationScope(as);
        }

        specified.possibly = specified.possibly || 
                possiblyAssignedAndNotDefinitelyExitedFromSomeCaseClause;
        specified.definitely = specified.definitely || 
                definitelyAssignedByEveryCaseClause;
        specified.definitelyExited = specified.definitelyExited && 
                definitelyExitedFromEveryCaseClause;
        specified.possiblyExited = specified.possiblyExited || 
                possiblyExitedFromSomeCaseClause;
        specified.definitelyByLoopBreaks = specified.definitelyByLoopBreaks && 
                specifiedByExitsFromEveryCaseClause;
        specified.possiblyByLoopBreaks = specified.possiblyByLoopBreaks && 
                specifiedByExitsFromSomeCaseClause;
        
        checkDeclarationSection(that);
    }
    
    @Override
    public void visit(Tree.WhileStatement that) {
        Tree.WhileClause whileClause = 
                that.getWhileClause();
        Tree.ConditionList conditionList = 
                whileClause.getConditionList();
        if (conditionList!=null) {
            conditionList.visit(this);
        }
        
        boolean d = beginDeclarationScope();
        SpecificationState as = beginSpecificationScope();
        Tree.Block block = whileClause.getBlock();
        if (block!=null) {
            if (isVariable() || isLate()) {
                block.visit(this);
            }
            else {
                boolean aolb = allOuterLoopsBreak;
                allOuterLoopsBreak = 
                        loopDepth==brokenLoopDepth;
                boolean broken = blockEndsInBreak(block);
                if (broken) {
                    brokenLoopDepth++;
                }
                loopDepth++;
                block.visit(this);
                if (broken) {
                    brokenLoopDepth--;
                }
                loopDepth--;
                allOuterLoopsBreak = aolb;
            }
        }
        
        boolean definitelyAssignedByWhileClause = 
                specified.definitely || 
                specified.definitelyExited;
        boolean possiblyAssignedByWhileClause = 
                specified.possibly;
        boolean definitelySpecifiedByLoopBreaks = 
                specified.definitelyByLoopBreaks;
        boolean possiblySpecifiedByLoopBreaks = 
                specified.possiblyByLoopBreaks;
        
        endDeclarationScope(d);
        endSpecificationScope(as);
        
        if (isAlwaysSatisfied(conditionList)) {
            specified.definitely = specified.definitely ||
                    definitelySpecifiedByLoopBreaks ||
                    definitelyAssignedByWhileClause;
            specified.possibly = specified.possibly || 
                    possiblyAssignedByWhileClause ||
                    possiblySpecifiedByLoopBreaks;
        }
        else if (isNeverSatisfied(conditionList)) {
            //nothing to do
        }
        else {
            specified.possibly = specified.possibly || 
                    possiblyAssignedByWhileClause ||
                    possiblySpecifiedByLoopBreaks;
        }
        
        checkDeclarationSection(that);
    }
    
    @Override
    public void visit(Tree.ForStatement that) {
        boolean d = beginDeclarationScope();
        SpecificationState as = beginSpecificationScope();
        boolean atLeastOneIteration = false;
        Tree.ForClause forClause = that.getForClause();
        Tree.Block block = forClause.getBlock();
        if (block!=null) {
            if (isVariable() || isLate()) {
                forClause.visit(this);
            }
            else {
                boolean aolb = allOuterLoopsBreak;
                allOuterLoopsBreak = 
                        loopDepth==brokenLoopDepth;
                boolean broken = blockEndsInBreak(block);
                if (broken) {
                    brokenLoopDepth++;
                }
                loopDepth++;
                forClause.visit(this);
                if (broken) {
                    brokenLoopDepth--;
                }
                loopDepth--;
                allOuterLoopsBreak = aolb;
            }
        }
        atLeastOneIteration = isAtLeastOne(forClause);
        boolean possiblyAssignedByForClause = 
                specified.possibly;
        boolean definitelyAssignedByForClause = 
                specified.definitely;
        boolean possiblyExitedFromForClause = 
                specified.possiblyExited;
        boolean definitelyExitedFromForClause = 
                specified.definitelyExited;
        boolean definitelySpecifiedByLoopBreaks = 
                specified.definitelyByLoopBreaks;
        boolean possiblySpecifiedByLoopBreaks = 
                specified.possiblyByLoopBreaks;
        
        endDeclarationScope(d);
        endSpecificationScope(as);

        boolean definitelyAssignedByElseClause;
        boolean possiblyAssignedByElseClause;
        boolean definitelyExitedFromElseClause;
        Tree.ElseClause elseClause = that.getElseClause();
        if (elseClause!=null) {
            d = beginDeclarationScope();
            as = beginSpecificationScope();
            elseClause.visit(this);
            definitelyAssignedByElseClause = 
                    specified.definitely || 
                    specified.definitelyExited;
            possiblyAssignedByElseClause = 
                    specified.possibly;
            definitelyExitedFromElseClause =
                    specified.definitelyExited;
            endDeclarationScope(d);
            endSpecificationScope(as);
        }
        else {
            definitelyAssignedByElseClause = false;
            possiblyAssignedByElseClause = false;
            definitelyExitedFromElseClause = false;
        }

        specified.definitely = specified.definitely || 
                !possiblyExitedFromForClause && 
                 definitelyAssignedByElseClause ||
                atLeastOneIteration && 
                definitelyAssignedByForClause ||
                definitelyAssignedByElseClause && 
                definitelySpecifiedByLoopBreaks;
        specified.possibly = specified.possibly || 
                possiblyAssignedByForClause &&
                !definitelyExitedFromForClause || 
                possiblyAssignedByElseClause &&
                !definitelyExitedFromElseClause ||
                possiblySpecifiedByLoopBreaks;
        
        checkDeclarationSection(that);
    }
      
}
