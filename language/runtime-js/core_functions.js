var BasicID=1;//used by identityHash

function getT$name() {return this.constructor.T$name;}
function getT$all() {return this.constructor.T$all;}
function initType(type, typeName) {
    var cons = function() {}
    type.$$ = cons;
    cons.T$name = typeName;
    cons.T$all = {}
    cons.T$all[typeName] = type;
    for (var i=2; i<arguments.length; ++i) {
        var superTypes = arguments[i].$$.T$all;
        for (var $ in superTypes) {cons.T$all[$] = superTypes[$]}
    }
    cons.prototype.getT$name = getT$name;
    cons.prototype.getT$all = getT$all;
}
function initTypeProto(type, typeName) {
    initType.apply(this, arguments);
    var args = [].slice.call(arguments, 2);
    args.unshift(type);
    inheritProto.apply(this, args);
}
function initTypeProtoI(type, typeName) {
  initType.apply(this, arguments);
  var args = [].slice.call(arguments, 2);
  if (type.$$.T$all['ceylon.language::Object']===undefined) {
    type.$$.T$all['ceylon.language::Object']=$_Object;
    //args.unshift($_Object);
  }
  args.unshift(type);
  inheritProto.apply(this, args);
}
function initExistingType(type, cons, typeName) {
    type.$$ = cons;
    cons.T$name = typeName;
    cons.T$all = {}
    cons.T$all[typeName] = type;
    for (var i=3; i<arguments.length; ++i) {
        var superTypes = arguments[i].$$.T$all;
        for (var $ in superTypes) {cons.T$all[$] = superTypes[$]}
    }
    var proto = cons.prototype;
    if (proto !== undefined) {
        try {
            proto.getT$name = getT$name;
            proto.getT$all = getT$all;
        } catch (exc) {
            // browser probably prevented access to the prototype
        }
    }
}
function initExistingTypeProto(type, cons, typeName) {
    var args = [].slice.call(arguments, 0);
    args.push($init$Basic());
    initExistingType.apply(this, args);
    var proto = cons.prototype;
    if ((proto !== undefined) && (proto.getHash === undefined)) {
    	var origToString = proto.toString;
        try {
            inheritProto(type, Basic);
            proto.toString = origToString;
        } catch (exc) {
            // browser probably prevented access to the prototype
        }
    }
}
function inheritProto(type) {
    var proto = type.$$.prototype;
    for (var i=1; i<arguments.length; ++i) {
        var superProto = arguments[i].$$.prototype;
        var names = Object.getOwnPropertyNames(superProto);
        for (var j=0; j<names.length; ++j) {
            var name = names[j];
            var desc = Object.getOwnPropertyDescriptor(superProto, name);
            // only copy own, enumerable properties
            if (desc && desc.enumerable) {
                if (desc.get) {
                    // defined through getter/setter, so copy the definition
                    Object.defineProperty(proto, name, desc);
                } else if (proto[name]===undefined || desc.value.$fml===undefined) {
                    proto[name] = desc.value;
                }
            }
        }
    }
}
// Define a property on the given object (which may be a prototype).
// "get" and "set" are getter/setter functions, and the latter is optional.
function atr$(obj, name, get, set, metamodel,settermm) {
  Object.defineProperty(obj, name, {get: get, set: set, configurable: true, enumerable: true});
  //if (name[0]==='$')name=name.substring(1);//names matching reserved words are prefixed with $
  obj['$prop$get'+name[0].toUpperCase()+name.substring(1)] = {get:get, set:set, $crtmm$:metamodel};
  if (settermm)set.$crtmm$=settermm;
}
// Create a copy of the given property. The name of the copied property is name+suffix.
// This is used in closure mode to provide access to inherited attribute implementations.
function copySuperAttr(obj, name, suffix) {
    var desc;
    var o = obj;
    // It may be an inherited property, so check the prototype chain.
    do {
        if ((desc = Object.getOwnPropertyDescriptor(o, name))) {break;}
        o = o.__proto__;
    } while (o);
    if (desc) {
        Object.defineProperty(obj, name+suffix, desc);
    }
}
// read/writeAttrib return the getter/setter for the given property as defined in the
// given type. This is used in prototype mode to access inherited attribute implementations.
function attrGetter(type, name) {
    return Object.getOwnPropertyDescriptor(type.$$.prototype, name).get;
}
function attrSetter(type, name, value) {
    return Object.getOwnPropertyDescriptor(type.$$.prototype, name).set;
}
ex$.initType=initType;
ex$.initTypeProto=initTypeProto;
ex$.initTypeProtoI=initTypeProtoI;
ex$.initExistingType=initExistingType;
ex$.initExistingTypeProto=initExistingTypeProto;
ex$.inheritProto=inheritProto;
ex$.atr$=atr$;
ex$.copySuperAttr=copySuperAttr;
ex$.attrGetter=attrGetter;
ex$.attrSetter=attrSetter;

//These are operators for handling nulls
//nonempty
function ne$(value){
  return value!==null&&value!==undefined&&is$(value,{t:Sequence});
}

function is$(obj,type){
  if(type && type.t){
    if(type.t==='i'||type.t==='u'){
      return isOfTypes(obj, type);
    }
    if(obj===null||obj===undefined){
      return type.t===Null||type.t===Anything;
    }
    if(obj.getT$all===undefined){
      if(obj.$crtmm$){
        var _mm=getrtmm$$(obj);
        //We can navigate the metamodel
        if(_mm.d['mt']==='m'){
          if(type.t===Callable){
            //It's a callable reference
            if(type.a&&type.a.Return$Callable&&_mm['$t']){
              //Check if return type matches
              if(extendsType(_mm['$t'],type.a.Return$Callable)){
                if(type.a.Arguments$Callable&&_mm['ps']!==undefined){
                  var metaparams=_mm['ps'];
                  if(metaparams.length==0){
                    return type.a.Arguments$Callable.t === Empty;
                  }else{
                    //check if arguments match
                    var comptype=type.a.Arguments$Callable;
                    for(var i=0;i<metaparams.length;i++){
                      if(comptype.t!==Tuple||!extendsType(metaparams[i]['$t'],comptype.a.First$Tuple)){
                        return false;
                      }
                      comptype=comptype.a.Rest$Tuple;
                    }
                  }
                }
                return true;
              }
            }
          }
        }
      }
      return false;
    }
    if (type.t==='T') {
      if (is$(obj,{t:Tuple})) {
        if (obj.$$targs$$ && obj.$$targs$$.t==='T') {
          if (type.l.length===obj.$$targs$$.l.length) {
            for (var i=0;i<type.l.length;i++) {
              if (!extendsType(obj.$$targs$$.l[i],type.l[i]))return false;
            }
            return true;
          } else {
            return false;
          }
        } else {
          type=retpl$(type);
        }
      } else {
        return false;
      }
    }
    if(type.t.$$.T$name in obj.getT$all()){
      if(type.t==Callable&&!(obj.$$targs$$ && obj.$$targs$$.Return$Callable && obj.$$targs$$.Arguments$Callable)
          && getrtmm$$(obj)&&obj.$crtmm$.$t && obj.$crtmm$.ps!==undefined){
        //Callable with no $$targs$$, we can build them from metamodel
        add_type_arg(obj,'Return$Callable',obj.$crtmm$.$t);
        add_type_arg(obj,'Arguments$Callable',{t:'T',l:[]});
        for(var i=0;i<obj.$crtmm$.ps.length;i++){
          obj.$$targs$$.Arguments$Callable.l.push(obj.$crtmm$.ps[i].$t);
        }
        if (obj.$$targs$$.Arguments$Callable.l.length===0)obj.$$targs$$.Arguments$Callable={t:Empty};
      }
      if(type.a && obj.$$targs$$) {
        for(var i in type.a) {
          var cmptype=type.a[i];
          var tmpobj=obj;
          var iance=cmptype.uv||null;
          var _mm=getrtmm$$(type.t);
          if(!iance&&_mm&&_mm.tp&&_mm.tp[i])iance=_mm.tp[i].dv;
          if(iance===null) {
            //null means no i in _mm.tp
            //Type parameter may be in the outer type
            while(iance===null&&tmpobj.outer$!==undefined){
              tmpobj=tmpobj.outer$;
              var _tmpf = tmpobj.constructor.T$all[tmpobj.constructor.T$name];
              var _mmf = getrtmm$$(_tmpf);
              if(_mmf&&_mmf.tp&&_mmf.tp[i]){
                iance=_mmf.tp[i].dv;
              }
              if(iance===null&&_mmf&&_mmf['super']){
                //lookup the type parameter in the supertype
                var smm=getrtmm$$(_mmf['super'].t);
                if(smm&&smm.tp&&smm.tp[i])iance=smm.tp[i].dv;
              }
              if(iance===null&&_mmf&&_mmf.sts){
                var sats=_mmf.sts;
                for(var s=0;iance===null&&s<sats.length;s++){
                  var smm=getrtmm$$(sats[s].t);
                  if (smm&&smm.tp&&smm.tp[i])iance=smm.tp[i].dv;
                }
              }
            }
          }
          if(iance===null) {
            //if the type has a container it could be a method
            //in which case the type parameter could be defined there
            var _omm=_mm;
            while(iance===null&&_omm) {
              if(_omm.tp&&_omm.tp[i]!==undefined){
                iance=_omm.tp[i].dv;
                tmpobj=obj;
              }
              if(iance===null)_omm=getrtmm$$(_omm.$cont);
            }
          }
          if (iance === 'out') {
            if (!extendsType(tmpobj.$$targs$$[i], cmptype,true)) {
              return false;
            }
          } else if (iance === 'in') {
            if (!extendsType(cmptype, tmpobj.$$targs$$[i],true)) {
              return false;
            }
          } else if (iance === undefined) {
            var _targ=tmpobj.$$targs$$[i];
            if (!(_targ && _targ.t && (_targ.t.$$ || _targ.t==='i' || _targ.t==='u')))return false;
            if (_targ.t.$$) {
              if (cmptype && cmptype.t && cmptype.t.$$) {
                if (!(cmptype.t.$$.T$name && _targ.t.$$.T$name === cmptype.t.$$.T$name))return false;
              } else if (cmptype && cmptype.t && cmptype.t==='i') {
                //_targ must satisfy all types in cmptype
                if (cmptype.t!==_targ.t || !cmptype.l || cmptype.l.length!==_targ.l.length)return false;
                for (var i=0; i<_targ.l.length;i++) {
                  if (!extendsType(_targ.l[i],cmptype,true))return false;
                }
              } else if (cmptype && cmptype.t && cmptype.t==='u') {
                //_targ must satisfy at least one type in cmptype
                if (cmptype.t!==_targ.t || !cmptype.l || cmptype.l.length!==_targ.l.length)return false;
                for (var i=0; i<_targ.l.length;i++) {
                  if (!extendsType(_targ.l[i],cmptype,true))return false;
                }
              }
            } else {
              if (cmptype.t!==_targ.t || !cmptype.l || cmptype.l.length!==_targ.l.length)return false;
              for (var i=0; i<_targ.l.length;i++) {
                if (!extendsType(_targ.l[i],cmptype,true))return false;
              }
            }
          } else if (iance === null) {
            console.log("Possible missing metamodel for " + type.t.$$.T$name + "<" + i + ">");
          } else {
            console.log("Don't know what to do about variance '" + iance + "'");
          }
        }
      }
      return true;
    }
  }
  return false;
}
function isOfTypes(obj, types) {
  if (obj===null) {
    for (var i=0; i < types.l.length; i++) {
      if(types.l[i].t===Null || types.l[i].t===Anything) return true;
      else if (types.l[i].t==='u') {
        if (isOfTypes(null, types.l[i])) return true;
      }
    }
    return false;
  }
  if (obj === undefined || obj.getT$all === undefined) { return false; }
  var unions = false;
  var inters = true;
  var _ints=false;
  var objTypes = obj.getT$all();
  for (var i = 0; i < types.l.length; i++) {
    var t = types.l[i];
    var partial = is$(obj, t);
    if (types.t==='u') {
      unions = partial || unions;
    } else {
      inters = partial && inters;
      _ints=true;
    }
  }
  return _ints ? inters||unions : unions;
}
//Tells whether t1 is a subtype of t2
//tparm indicates if the calculations are being done on type parameters
function extendsType(t1, t2,tparm) {
    if (t1 === undefined || t1.t === undefined || t1.t === Nothing || t2 === undefined || t2.t === undefined) {
      return true;//t2 === undefined;
    } else if (t2 && t2.t === Anything) {
      return true;
    } else if (t1 === null) {
      return t2.t === Null || t2.t === Anything;
    }
    if (t1.t === 'u' || t1.t === 'i') {
        if (t1.t==='i')removeSupertypes(t1.l);
        var unions = false;
        var inters = true;
        var _ints = false;
        for (var i = 0; i < t1.l.length; i++) {
            var partial = extendsType(t1.l[i],t2,tparm);
            if (t1.t==='u'&&!tparm) {
                unions = partial||unions;
            } else {
                inters = partial&&inters;
                _ints=true;
            }
        }
        return _ints ? inters||unions : unions;
    }
    if (t2.t === 'u' || t2.t === 'i') {
        if (t2.t==='i')removeSupertypes(t2.l);
        var unions = false;
        var inters = true;
        var _ints = false;
        for (var i = 0; i < t2.l.length; i++) {
            var partial = extendsType(t1, t2.l[i],tparm);
            if (t2.t==='u') {
                unions = partial||unions;
            } else {
                inters = partial&&inters;
                _ints=true;
            }
        }
        return _ints ? inters||unions : unions;
    }
    if (t1.t==='T') {
      if (t2.t==='T') {
        if (t1.l.length>=t2.l.length) {
          for (var i=0; i < t2.l.length;i++) {
            if (!extendsType(t1.l[i],t2.l[i],tparm))return false;
          }
          return true;
        } else return false;
      } else {
        t1=retpl$(t1);
      }
    } else if (t2.t==='T') {
      t2=retpl$(t2);
    }
    for (t in t1.t.$$.T$all) {
        if (t === t2.t.$$.T$name || t === 'ceylon.language::Nothing') {
            if (t1.a && t2.a) {
                //Compare type arguments
                for (ta in t1.a) {
                    if (!extendsType(t1.a[ta], t2.a[ta],tparm)) return false;
                }
            }
            return true;
        }
    }
    return false;
}
function removeSupertypes(list) {
    for (var i=0; i < list.length; i++) {
        for (var j=i; i < list.length; i++) {
            if (i!==j) {
                if (extendsType(list[i],list[j])) {
                    list[j]=list[i];
                } else if (extendsType(list[j],list[i])) {
                    list[i]=list[j];
                }
            }
        }
    }
}

function ut$(a,b){
  if (a.t==='u'){
  } else if (b.t==='u') {
  } else if (a.t==='i'||b.t==='u') {
  } else if (a.t===b.t) {
    if (a.a&&b.a) {
      return {t:a.t,a:it$(a.a,b.a)};
    }
    return a;
  }
  return {t:'u',l:[a,b]};
}
function it$(a,b){
if (!b) {
throw new Error("QUE PEDO");
}
  if (a.t==='i') {
    //add to the current intersection if not present
  } else if (b.t==='i') {
    //create a new intersection with list of a+list of b's
  } else if (a.t==='u'||b.t==='u') {
    //Create new intersection
  } else if (a.t===b.t) {
    if (a.a&&b.a) {
      return {t:a.t,a:ut$(a.a,b.a)};
    }
    return a;
  }
  return {t:'i',l:[a,b]};
}
function set_type_args(obj,targs,t) {
  if (obj===undefined)return;
  if (obj.$$targs$$ === undefined) {
    obj.$$targs$$={};
  }
  for (x in targs) {
    var n=targs[x];
    if (!n)continue;
    var e=obj.$$targs$$[x];
    if (e) {
      var mm=getrtmm$$(t);
      var iance=mm && mm.tp && mm.tp[x] && mm.tp[x].dv;
      if (iance === 'out') {
        //Intersection
        n=it$(e,n);
      } else if (iance === 'in') {
        //Union
        n=ut$(e,n);
      }
    }
    obj.$$targs$$[x] = n;
  }
}
function add_type_arg(obj, name, type) {
    if (obj===undefined)return;
    if (obj.$$targs$$ === undefined) {
        obj.$$targs$$={};
    }
    obj.$$targs$$[name]=type;
}
function wrapexc(e,loc,file) {
  if (loc !== undefined) e.$loc=loc;
  if (file !== undefined) e.$file=file;
  return e;
}
function throwexc(e,loc,file) {
  if (loc !== undefined) e.$loc=loc;
  if (file !== undefined) e.$file=file;
  throw e;
}
//exists
function nn$(e) {
  return e!==null&&e!==undefined;
}
//for optimized ranges
//"exit optimized range loop"
function eorl$(comp) {
  if (comp===getEqual())return function(a,b){return a.compare(b)===getEqual();}
  return function(a,b){return a.compare(b)!==comp;}
}
//for shorter assert
function asrt$(cond,msg,loc,file) {
  if (!cond)throw wrapexc(AssertionError(msg),loc,file);
}
ex$.set_type_args=set_type_args;
ex$.add_type_arg=add_type_arg;
ex$.ne$=ne$;
ex$.is$=is$;
ex$.throwexc=throwexc;
ex$.wrapexc=wrapexc;
ex$.nn$=nn$;
ex$.eorl$=eorl$;
ex$.asrt$=asrt$;
