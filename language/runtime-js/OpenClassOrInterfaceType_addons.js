atr$(OpenClassOrInterfaceType$meta$declaration.$$.prototype,'typeArguments',function(){
  var tps=this.declaration.tipo.$crtmm$.tp;
  if (tps) {
    var rtps = this.declaration._targs;
    var targs={};
    for (var tpn in tps) {
      var rtp=rtps&&rtps[tpn];
      var otp=OpenTypeParam$jsint(this.declaration.tipo,tpn);
      var targ;
      if (rtp===undefined) {
        targ = OpenTvar$jsint(otp);
      } else if (typeof(rtp)==='string') {
        targ = OpenTvar$jsint(OpenTypeParam$jsint(this.declaration.tipo,rtp));
      } else {
        if (rtp.t==='i'||rtp.t==='u') {
          //resolve case types
          var nrtp={t:rtp.t,l:[]};
          for (var i=0;i<rtp.l.length;i++) {
            var _ct=rtp.l[i];
            nrtp.l.push(typeof(_ct)==='string'?OpenTvar$jsint(OpenTypeParam$jsint(this.declaration.tipo,_ct)):_ct);
          }
          rtp=nrtp;
        }
        targ = _openTypeFromTarg(rtp,this.declaration);
      }
      targs[otp.qualifiedName]=[otp,targ];
    }
    return Mapita(targs,{V$Mapita:{t:OpenType$meta$declaration}});
  }
  return getEmpty();
},undefined,function(){return{mod:$CCMM$,$t:{t:Map,a:{Key:{t:TypeParameter$meta$declaration},Item:{t:OpenType$meta$declaration}}},$cont:OpenClassOrInterfaceType,an:function(){return[shared(),actual()];},d:['ceylon.language.meta.declaration','OpenClassOrInterfaceType','$at','typeArguments']};});
atr$(OpenClassOrInterfaceType$meta$declaration.$$.prototype,'extendedType',function(){
  return this.declaration.extendedType;
},undefined,function(){return{mod:$CCMM$,$t:{ t:'u', l:[{t:Null},{t:OpenClassType$meta$declaration}]},$cont:OpenClassOrInterfaceType,an:function(){return[shared(),actual()];},d:['ceylon.language.meta.declaration','OpenClassOrInterfaceType','$at','extendedType']};});
atr$(OpenClassOrInterfaceType$meta$declaration.$$.prototype,'declaration',function(){return this._decl;},undefined,function(){return{mod:$CCMM$,$t:{t:ClassDeclaration$meta$declaration},$cont:OpenClassOrInterfaceType,an:function(){return[shared(),actual()];},d:['ceylon.language.meta.declaration','OpenClassOrInterfaceType','$at','declaration']};});
atr$(OpenClassOrInterfaceType$meta$declaration.$$.prototype,'satisfiedTypes',function(){
  return this.declaration.satisfiedTypes;
},undefined,function(){return{mod:$CCMM$,$t:{t:Sequential,a:{Element$Sequential:{t:OpenInterfaceType$meta$declaration}}},$cont:OpenClassOrInterfaceType$meta$declaration,an:function(){return[shared(),actual()];},d:['ceylon.language.meta.declaration','OpenClassOrInterfaceType','$at','satisfiedTypes']};});
atr$(OpenClassOrInterfaceType$meta$declaration.$$.prototype,'string',function(){
  var s=this.declaration.qualifiedName;
  var tps=this.declaration.tipo.$crtmm$.tp;
  if (tps) {
    var rtps=this.declaration._targs;
    s+="<";
    var first=true;
    for (var t in tps) {
      var rtp=rtps&&rtps[t];
      if (first)first=false;else s+=",";
      if (rtp===undefined||typeof(rtp)==='string') {
        if(typeof(rtp)==='string')t=rtp;
        if (t.indexOf('$')>0)t=t.substring(0,t.indexOf('$'));
        s+=t;
      } else {
        s+=_openTypeFromTarg(rtp).string;
      }
    }
    s+=">";
  }
  return s;
},undefined,function(){return{mod:$CCMM$,$t:{t:$_String},d:['$','Object','$at','string']};});
