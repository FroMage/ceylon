atr$(Member$meta$model.$$.prototype,'declaringType',function(){
  var $$member=this;
  var mm = getrtmm$$($$member.tipo);
  var m2 = get_model(mm);
  return (m2['mt']==='c'?OpenClass:OpenInterface)(getModules$meta().find(mm.mod['$mod-name'],mm.mod['$mod-version']).findPackage(mm.d[0]), $$member.tipo);
},undefined,function(){return{mod:$CCMM$,$t:{t:Member$meta$model,a:{Type:{t:Anything}}},$cont:Member$meta$model,an:function(){return[shared(),formal()];},d:['ceylon.language.meta.model','Member','$at','declaringType']};});

atr$(Member$meta$model.$$.prototype,'container',function(){
    return this.declaringClassOrInterface;
},undefined,function(){return{mod:$CCMM$,$t:{ t:'u', l:[{t:Null},{t:Type$meta$model,a:{Type:{t:Anything}}}]},$cont:Member$meta$model,an:function(){return[shared(),actual()];},d:['ceylon.language.meta.model','Member','$at','container']};});
