function(anntypes,$m) {
  var mm=getrtmm$$(this.tipo);
  var pref=mm.d[mm.d.length-1];
  var cs=coiclsannconstrs$(this,anntypes,pref+"$c_",this.$$targs$$.Type$AppliedClass,$m.Arguments$getDeclaredCallableConstructors);
  if (cs.length===0) {
    cs=coiclsannconstrs$(this,anntypes,pref+"_",this.$$targs$$.Type$AppliedClass,$m.Arguments$getDeclaredCallableConstructors);
  }
  for (var i=0;i<cs.length;i++) {
    var r=AppliedCallableConstructor$jsint(cs[i].tipo,{Type$AppliedCallableConstructor:this.$$targs$$.Type$AppliedClass,
                Arguments$AppliedCallableConstructor:cs[i].args},undefined,this.$targs);
    r.cont$=this;
    if (cs[i].fakeConstr)r.fakeConstr$=true;
    cs[i]=r;
  }
  return $arr$sa$(cs,{t:CallableConstructor$meta$model,
    a:{Type$CallableConstructor:this.$$targs$$.Type$AppliedClass,Arguments$CallableConstructor:{t:Nothing}}});
}
