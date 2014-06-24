function $_Boolean(value) {return Boolean(value)}
initExistingTypeProto($_Boolean, Boolean, 'ceylon.language::Boolean');
$_Boolean.$crtmm$={ps:[],an:function(){return[shared(),abstract()]},mod:$CCMM$,d:['$','Boolean']};
ex$.$_Boolean=$_Boolean;
function trueClass() {}
initType(trueClass, "ceylon.language::true", $_Boolean);
function falseClass() {}
initType(falseClass, "ceylon.language::false", $_Boolean);
Boolean.prototype.getT$name = function() {
    return (this.valueOf()?trueClass:falseClass).$$.T$name;
}
Boolean.prototype.getT$all = function() {
    return (this.valueOf()?trueClass:falseClass).$$.T$all;
}
Boolean.prototype.equals = function(other) {return other.constructor===Boolean && other==this;}
atr$(Boolean.prototype, 'hash', function(){ return this.valueOf()?1:0; },
  undefined,{an:function(){return[shared(),actual()]},mod:$CCMM$,d:['$','Object','$at','hash']});
atr$(Boolean.prototype, 'string', function(){ return this.valueOf()?"true":"false"; },
  undefined,{an:function(){return[shared(),actual()]},mod:$CCMM$,d:['$','Object','$at','string']});

atr$(Boolean.prototype, 'not', function(){ return !this.valueOf(); },
  undefined,function(){return{an:function(){return[shared(),actual()]},mod:$CCMM$,$cont:Binary,d:['$','Binary','$at','not']};});
Boolean.prototype.leftLogicalShift = function(i) { return this.valueOf(); }
Boolean.prototype.rightLogicalShift = function(i) { return this.valueOf(); }
Boolean.prototype.rightArithmeticShift = function(i) { return this.valueOf(); }
Boolean.prototype.and = function(x) { return this.valueOf() && x.valueOf(); }
Boolean.prototype.or = function(x) { return this.valueOf() || x.valueOf(); }
Boolean.prototype.xor = function(x) { return this.valueOf()?!x.valueOf():x.valueOf(); }
Boolean.prototype.$_get = function(idx) {
    return idx === 0 ? this.valueOf() : false;
}
Boolean.prototype.set = function(idx,bit) {
    if (bit === undefined) { bit = true; }
    return idx === 0 ? bit.valueOf() : this.valueOf();
}
Boolean.prototype.flip = function(idx) {
    return idx === 0 ? !this.valueOf() : this.valueOf();
}
Boolean.prototype.clear = function(index) {
    return index === 0 ? false : this.valueOf();
}

function getTrue() {return true;}
function getFalse() {return false;}
ex$.$prop$getTrue={get:getTrue,$crtmm$:function(){return{mod:$CCMM$,d:['$','true'],$t:{t:$_Boolean}};}};
ex$.$prop$getFalse={get:getFalse,$crtmm$:function(){return{mod:$CCMM$,d:['$','false'],$t:{t:$_Boolean}};}};

//Map with TypeParameters as keys
function Mapita(o,$$targs$$,$$mapita){
    $init$Mapita();
    if($$mapita===undefined)$$mapita=new Mapita.$$;
    set_type_args($$mapita,$$targs$$);
    Map({Item$Map:$$targs$$.V$Mapita,Key$Map:{t:TypeParameter$meta$declaration}},$$mapita);
    $$mapita.o=o;
    
    //AttributeDecl hash at caca.ceylon (6:2-6:56)
    $$mapita.$prop$getHash={$crtmm$:function(){return{mod:$CCMM$,$t:{t:Integer},$cont:Mapita,an:function(){return[shared(),actual()];},d:['$','Map','$at','hash']};}};
    $$mapita.$prop$getHash.get=function(){return hash};
    return $$mapita;
}
Mapita.$crtmm$=function(){return{mod:$CCMM$,'super':{t:Basic},ps:[],tp:{V$Mapita:{'var':'out','satisfies':[{t:$_Object}]}},satisfies:[{t:Map,a:{Item$Map:'V$Mapita',Key$Map:{t:TypeParameter$meta$declaration}}}],pa:1,d:['','Mapita']};};
ex$.Mapita=Mapita;
function $init$Mapita(){
    if(Mapita.$$===undefined){
        initTypeProto(Mapita,'Mapita',Basic,$init$Map());
        (function($$mapita){
            
            //MethodDecl equals at caca.ceylon (5:2-5:81)
            $$mapita.equals=function (other$2){
                var $$mapita=this;
                return $$mapita.getT$all()['ceylon.language::Map'].$$.prototype.equals.call(this,other$2);
            };
            $$mapita.equals.$crtmm$=function(){return{mod:$CCMM$,$t:{t:$_Boolean},ps:[{nm:'other',mt:'prm',$t:{t:$_Object},an:function(){return[];}}],$cont:Mapita,an:function(){return[shared(),actual()];},d:['$','Map','$m','equals']};};
            
            //AttributeDecl hash at caca.ceylon (6:2-6:56)
            atr$($$mapita,'hash',function(){
                var $$mapita=this;
                return attrGetter($$mapita.getT$all()['ceylon.language::Map'],'hash').call(this);
            },undefined,function(){return{mod:$CCMM$,$t:{t:Integer},$cont:Mapita,an:function(){return[shared(),actual()];},d:['$','Map','$at','hash']};});
            
            //MethodDecl clone at caca.ceylon (7:2-7:43)
            $$mapita.clone=function (){
                return this;
            };
            $$mapita.clone.$crtmm$=function(){return{mod:$CCMM$,$t:{t:Mapita,a:{V$Mapita:'V$Mapita'}},ps:[],$cont:Mapita,an:function(){return[shared(),actual()];},d:['','Mapita','$m','clone']};};
            
            //MethodDef iterator at caca.ceylon (8:2-10:2)
            $$mapita.iterator=function iterator(){
                var $$mapita=this;

                function miter$3($$targs$$){
                    var $$miter$3=new miter$3.$$;
                    $$miter$3.$$targs$$=$$targs$$;
                    Iterator({Element$Iterator:{t:Entry,a:{Item$Entry:$$targs$$.V$Mapita,Key$Entry:{t:TypeParameter$meta$declaration}}}},$$miter$3);
                    add_type_arg($$miter$3,'Element$Iterator',{t:Entry,a:{Item$Entry:$$mapita.$$targs$$.V$Mapita,Key$Entry:{t:TypeParameter$meta$declaration}}});
                    
                    //AttributeDecl keys at caca.ceylon (10:6-10:33)
                    $$miter$3.keys=[];
                    for (var k in $$mapita.o) $$miter$3.keys.push(k);
                    
                    //AttributeDecl idx at caca.ceylon (11:6-11:27)
                    $$miter$3.idx=-1;
                    return $$miter$3;
                };miter$3.$crtmm$=function(){return{mod:$CCMM$,'super':{t:Basic},satisfies:[{t:Iterator,a:{Element$Iterator:{t:Entry,a:{Item$Entry:'V$Mapita',Key$Entry:{t:TypeParameter$meta$declaration}}}}}],d:['','Mapita','$m','iterator','$o','miter']};};
                function $init$miter$3(){
                    if(miter$3.$$===undefined){
                        initTypeProto(miter$3,'Mapita.iterator.miter',Basic,Iterator);
                        (function($$miter$3){
                            //MethodDef next at caca.ceylon (12:6-18:6)
                            $$miter$3.next=function next(){
                                var $$miter=this;
                                $$miter.idx++;
                                if ($$miter.idx>=$$miter.keys.length)return getFinished();
                                var k$8=$$miter.keys[$$miter.idx];
                                var v$8=$$mapita.o[k$8]||null;
                                return Entry(v$8[0],v$8[1],{Key$Entry:{t:TypeParameter$meta$declaration},Item$Entry:$$mapita.$$targs$$.V$mapita});
                            };$$miter$3.next.$crtmm$=function(){return{mod:$CCMM$,$t:{t:'u', l:[{t:Entry,a:{Item$Entry:'V$Mapita',Key$Entry:{t:TypeParameter$meta$declaration}}},{t:Finished}]},ps:[],$cont:miter$3,an:function(){return[shared(),actual()];},d:['','Mapita','$m','iterator','$o','miter','$m','next']};};
                        })(miter$3.$$.prototype);
                    }
                    return miter$3;
                }
                $init$miter$3();
                var miter$9;
                function getMiter$9(){
                    if(miter$9===undefined){miter$9=$init$miter$3()({Element$Iterator:{t:Entry,a:{Item$Entry:$$mapita.$$targs$$.V$Mapita,Key$Entry:{t:TypeParameter$meta$declaration}}}});miter$9.$crtmm$=getMiter$9.$crtmm$;}
                    return miter$9;
                }
                getMiter$9.$crtmm$=function(){return{mod:$CCMM$,$t:{t:miter$3},d:['','Mapita','$m','iterator','$o','miter']};};
                $prop$getMiter$9={get:getMiter$9,$crtmm$:getMiter$9.$crtmm$};
                return getMiter$9();
            };$$mapita.iterator.$crtmm$=function(){return{mod:$CCMM$,$t:{t:Iterator,a:{Element$Iterator:{t:Entry,a:{Item$Entry:'V$Mapita',Key$Entry:{t:TypeParameter$meta$declaration}}}}},ps:[],$cont:Mapita,an:function(){return[shared(),actual()];},d:['','Mapita','$m','iterator']};};
            
            //MethodDef get at caca.ceylon (11:2-13:2)
            $$mapita.$_get=function $_get(k$3){
              var v=this.o[k$3.qualifiedName];
              return v===undefined?null:v[1];
            };$$mapita.$_get.$crtmm$=function(){return{mod:$CCMM$,$t:'V$Mapita',ps:[{nm:'k',mt:'prm',$t:{t:$_Object},an:function(){return[];}}],$cont:Mapita,an:function(){return[shared(),actual()];},d:['','Mapita','$m','get']};};
        })(Mapita.$$.prototype);
    }
    return Mapita;
}
ex$.$init$Mapita=$init$Mapita;
$init$Mapita();
