package com.redhat.ceylon.compiler.typechecker.analyzer;

import static com.redhat.ceylon.compiler.typechecker.analyzer.Util.name;

import java.util.ArrayList;
import java.util.List;

import com.redhat.ceylon.compiler.typechecker.context.Context;
import com.redhat.ceylon.compiler.typechecker.model.BottomType;
import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Interface;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ClassBody;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Statement;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

/**
 * Bucket for some helper methods used by various
 * visitors.
 * 
 * @author Gavin King
 *
 */
public abstract class AbstractVisitor extends Visitor {

    protected abstract Context getContext();
    
    /**
     * Search for the declaration referred to by a qualified 
     * name in containing scopes and imports, finally looking 
     * in the language module.
     * 
     *  TODO: I would just love to make this method go away
     *        by somehow treating declarations in the language 
     *        module like any other imported declaration!
     *        
     */
    private Declaration getBaseDeclaration(Scope scope, Unit unit, String name) {
        Declaration d = scope.getMemberOrParameter(unit, name);
        if (d!=null) {
            return d;
        }
        else {
            return getLanguageModuleDeclaration(name);
        }
    }
    
    protected TypedDeclaration getBaseDeclaration(Tree.BaseMemberExpression bme) {
        return (TypedDeclaration) getBaseDeclaration(bme.getScope(), bme.getUnit(), name(bme.getIdentifier()));
    }
    
    protected TypeDeclaration getBaseDeclaration(Tree.BaseType bt) {
        return (TypeDeclaration) getBaseDeclaration(bt.getScope(), bt.getUnit(), name(bt.getIdentifier()));
    }
    
    protected TypeDeclaration getBaseDeclaration(Tree.BaseTypeExpression bte) {
        return (TypeDeclaration) getBaseDeclaration(bte.getScope(), bte.getUnit(), name(bte.getIdentifier()));
    }
    
    /**
     * Search for a declaration in the language module. 
     */
    private Declaration getLanguageModuleDeclaration(String name) {
        //all elements in ceylon.language are auto-imported
        //traverse all default module packages provided they have not been traversed yet
        Module languageModule = getContext().getModules().getLanguageModule();
        if ( languageModule != null && languageModule.isAvailable() ) {
            if ("Bottom".equals(name)) {
                return new BottomType();
            }
            for (Package languageScope : languageModule.getPackages() ) {
                Declaration d = languageScope.getMember(name);
                if (d != null) {
                    return d;
                }
            }
        }
        return null;
    }
    
    protected Interface getCorrespondenceDeclaration() {
        return (Interface) ((TypeDeclaration) getLanguageModuleDeclaration("Correspondence"));
    }

    protected Class getVoidDeclaration() {
        return (Class) ((TypeDeclaration) getLanguageModuleDeclaration("Void"));
    }
    
    protected Class getNothingDeclaration() {
        return (Class) ((TypeDeclaration) getLanguageModuleDeclaration("Nothing"));
    }

    protected Value getNullDeclaration() {
        return (Value) getLanguageModuleDeclaration("null");
    }

    protected Interface getEmptyDeclaration() {
        return (Interface) ((TypeDeclaration) getLanguageModuleDeclaration("Empty"));
    }

    protected Interface getSequenceDeclaration() {
        return (Interface) ((TypeDeclaration) getLanguageModuleDeclaration("Sequence"));
    }

    protected Class getObjectDeclaration() {
        return (Class) ((TypeDeclaration) getLanguageModuleDeclaration("Object"));
    }
    
    protected Class getIdentifiableObjectDeclaration() {
        return (Class) ((TypeDeclaration) getLanguageModuleDeclaration("IdentifiableObject"));
    }
    
    protected Class getExceptionDeclaration() {
        return (Class) ((TypeDeclaration) getLanguageModuleDeclaration("Exception"));
    }

    protected Interface getCategoryDeclaration() {
        return (Interface) ((TypeDeclaration) getLanguageModuleDeclaration("Category"));
    }
    
    protected Interface getIterableDeclaration() {
        return (Interface) ((TypeDeclaration) getLanguageModuleDeclaration("Iterable"));
    }
    
    protected Interface getCastableDeclaration() {
        return (Interface) ((TypeDeclaration) getLanguageModuleDeclaration("Castable"));
    }
    
    protected Interface getSummableDeclaration() {
        return (Interface) ((TypeDeclaration) getLanguageModuleDeclaration("Summable"));
    }
        
    protected Interface getNumericDeclaration() {
        return (Interface) ((TypeDeclaration) getLanguageModuleDeclaration("Numeric"));
    }
        
    protected Interface getIntegralDeclaration() {
        return (Interface) ((TypeDeclaration) getLanguageModuleDeclaration("Integral"));
    }
        
    protected Interface getInvertableDeclaration() {
        return (Interface) ((TypeDeclaration) getLanguageModuleDeclaration("Invertable"));
    }
        
    protected Interface getSlotsDeclaration() {
        return (Interface) ((TypeDeclaration) getLanguageModuleDeclaration("Slots"));
    }
        
    protected TypeDeclaration getComparisonDeclaration() {
        return (TypeDeclaration) getLanguageModuleDeclaration("Comparison");
    }
        
    protected TypeDeclaration getBooleanDeclaration() {
        return (TypeDeclaration) getLanguageModuleDeclaration("Boolean");
    }
        
    protected TypeDeclaration getStringDeclaration() {
        return (TypeDeclaration) getLanguageModuleDeclaration("String");
    }
        
    protected TypeDeclaration getFloatDeclaration() {
        return (TypeDeclaration) getLanguageModuleDeclaration("Float");
    }
        
    protected TypeDeclaration getNaturalDeclaration() {
        return (TypeDeclaration) getLanguageModuleDeclaration("Natural");
    }
        
    protected TypeDeclaration getCharacterDeclaration() {
        return (TypeDeclaration) getLanguageModuleDeclaration("Character");
    }
        
    protected TypeDeclaration getQuotedDeclaration() {
        return (TypeDeclaration) getLanguageModuleDeclaration("Quoted");
    }
        
    protected TypeDeclaration getFormatDeclaration() {
        return (TypeDeclaration) getLanguageModuleDeclaration("Format");
    }
        
    protected Interface getEqualityDeclaration() {
        return (Interface) ((TypeDeclaration) getLanguageModuleDeclaration("Equality"));
    }
        
    protected Interface getComparableDeclaration() {
        return (Interface) ((TypeDeclaration) getLanguageModuleDeclaration("Comparable"));
    }
        
    protected Interface getCloseableDeclaration() {
        return (Interface) ((TypeDeclaration) getLanguageModuleDeclaration("Closeable"));
    }
        
    protected Interface getOrdinalDeclaration() {
        return (Interface) ((TypeDeclaration) getLanguageModuleDeclaration("Ordinal"));
    }
        
    protected Class getRangeDeclaration() {
        return (Class) ((TypeDeclaration) getLanguageModuleDeclaration("Range"));
    }
        
    protected Class getEntryDeclaration() {
        return (Class) ((TypeDeclaration) getLanguageModuleDeclaration("Entry"));
    }
    
    protected void checkTypeBelongsToContainingScope(ProducedType type,
            Scope s, Node that) {
        //TODO: this does not account for types 
        //      inherited by a containing scope!
        //TODO: what if the type arguments don't match?!
        while (s!=null) {
            if (type.getDeclaration().getContainer()==s) {
                return;
            }
            s=s.getContainer();
        }
        that.addError("illegal use of qualified type outside scope of qualifying type: " + 
                type.getProducedTypeName());
    }

    protected static List<ProducedType> getTypeArguments(Tree.TypeArguments tal) {
        List<ProducedType> typeArguments = new ArrayList<ProducedType>();
        if (tal instanceof Tree.TypeArgumentList) {
            for (Tree.Type ta: ( (Tree.TypeArgumentList) tal ).getTypes()) {
                ProducedType t = ta.getTypeModel();
                if (t==null) {
                    ta.addError("could not resolve type argument");
                    typeArguments.add(null);
                }
                else {
                    typeArguments.add(t);
                }
            }
        }
        return typeArguments;
    }
    
    protected Tree.Statement getLastExecutableStatement(Tree.ClassBody that) {
        List<Statement> statements = that.getStatements();
        for (int i=statements.size()-1; i>=0; i--) {
            Tree.Statement s = statements.get(i);
            if (s instanceof Tree.ExecutableStatement) {
                return s;
            }
            else {
                if (s instanceof Tree.AttributeDeclaration) {
                    if ( ((Tree.AttributeDeclaration) s).getSpecifierOrInitializerExpression()!=null ) {
                        return s;
                    }
                }
                if (s instanceof Tree.MethodDeclaration) {
                    if ( ((Tree.MethodDeclaration) s).getSpecifierExpression()!=null ) {
                        return s;
                    }
                }
                if (s instanceof Tree.ObjectDefinition) {
                    Tree.ObjectDefinition o = (Tree.ObjectDefinition) s;
                    if (o.getExtendedType()!=null) {
                        ProducedType et = o.getExtendedType().getType().getTypeModel();
                        if (et!=null 
                                && et.getDeclaration()!=getObjectDeclaration()
                                && et.getDeclaration()!=getIdentifiableObjectDeclaration()) {
                            return s;
                        }
                    }
                    if (o.getClassBody()!=null) {
                        if (getLastExecutableStatement(o.getClassBody())!=null) {
                            return s;
                        }
                    }
                }
            }
        }
        return null;
    }
            
}
