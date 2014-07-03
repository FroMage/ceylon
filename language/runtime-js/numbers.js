function toInt(f) {
  return (f >= 0) ? Math.floor(f) : Math.ceil(f);
}
function nflt$(n) {
  if (n.float$)return true;
  var r= n===+n&&n!==(n|0);
  if (!r && n.float$===undefined)r=Math.round(n)!=n;
  return r;
}

function JSNumber(value) {
  if (value===parseInt(value)) {
    return Integer(value);
  }
  return Float(value);
}
initExistingType(JSNumber, Number, 'ceylon.language::JSNumber');
JSNumber.$crtmm$=function(){return{nm:'JSNumber',mt:'c',pa:1,
  mod:$CCMM$,d:['$','Number']};}

var origNumToString = Number.prototype.toString;
inheritProto(JSNumber, $_Object, $_Number, $init$Integral(), Exponentiable);

function Integer(value) {
    if (value && value.getT$name && value.getT$name() === 'ceylon.language::Integer') {
        return value;
    }
    var that=Number(value);
    that.float$=false;
    return that;
}
initTypeProto(Integer, 'ceylon.language::Integer', $_Object,$_Number,
        $init$Integral(), Exponentiable, Binary);
Integer.$crtmm$=function(){return{an:function(){return[shared(),$_native(),$_final()];},mod:$CCMM$,d:['$','Integer']};}

function Float(value) {
    if (value && value.getT$name && value.getT$name() === 'ceylon.language::Float') {
        return value;
    }
    var that = new Number(value);
    that.float$ = true;
    return that;
}
initTypeProto(Float, 'ceylon.language::Float', $_Object,$_Number,Exponentiable);
Float.$crtmm$=function(){return{an:function(){return[shared(),$_native(),$_final()];},mod:$CCMM$,d:['$','Float']};}

var JSNum$proto = Number.prototype;
JSNum$proto.getT$all = function() {
    return (nflt$(this) ? Float : Integer).$$.T$all;
}
JSNum$proto.getT$name = function() {
    return (nflt$(this) ? Float : Integer).$$.T$name;
}
var mock$flt={},mock$int={};
$_Number({Other$Number:{t:Float}},mock$flt);
$_Number({Other$Number:{t:Integer}},mock$int);
atr$(JSNum$proto,'$$targs$$',function(){
  if (!this.$targs$)this.$targs$=(nflt$(this)?mock$flt:mock$int).$$targs$$;
  return this.$targs$;
});
JSNum$proto.toString = origNumToString;
atr$(JSNum$proto, 'string', function(){ return this.toString(); },
  undefined,function(){return{an:function(){return[shared(),actual()]},mod:$CCMM$,$cont:$_Object,d:['$','Object','$at','string']};});
function $addnm$(nm,s) {
  var t={t:JSNumber};
  if (s===undefined)s=$_Number.$$.prototype[nm];
  var d=JSNum$proto[nm];
  d.$crtmm$=s.$crtmm$();
  d=d.$crtmm$;
  if (typeof(d.$t)==='string')d.$t=t;
  if (d.ps) for (var i=0; i < d.ps.length; i++) {
    if (typeof(d.ps[i].$t)==='string')d.ps[i].$t=t;
  }
}

JSNum$proto.plus = function(other) {
    return (nflt$(this)||nflt$(other)) ? Float(this+other) : (this+other);
}
$addnm$('plus');
JSNum$proto.plusInteger = function(other) {
    return nflt$(this) ? Float(this+other) : (this+other);
}
$addnm$('plusInteger');
JSNum$proto.minus = function(other) {
    return (nflt$(this)||nflt$(other)) ? Float(this-other) : (this-other);
}
$addnm$('minus');
JSNum$proto.times = function(other) {
    return (nflt$(this)||nflt$(other)) ? Float(this*other) : (this*other);
}
$addnm$('times');
JSNum$proto.timesInteger = function(other) {
    return nflt$(this) ? Float(this*other) : (this*other);
}
$addnm$('timesInteger');
JSNum$proto.divided = function(other) {
    if (nflt$(this)||nflt$(other)) { return Float(this/other); }
    if (other == 0) {
        throw Exception("Division by Zero");
    }
    return toInt(this/other);
}
$addnm$('divided');
JSNum$proto.remainder = function(other) { return this%other; }
$addnm$('remainder',Integral.$$.prototype.remainder);
JSNum$proto.remainder.$crtmm$=function(){return{mod:$CCMM$,$t:{t:Integer},ps:[{$t:{t:Integer},nm:'other'}],d:['$','Integer','remainder']};}
JSNum$proto.power = function(exp) {
    if (nflt$(this)||nflt$(exp)) { return Float(Math.pow(this, exp)); }
    if (exp<0 && this!=1 && this!=-1) {
        throw AssertionError("Negative exponent");
    }
    return toInt(Math.pow(this, exp));
}
$addnm$('power',Exponentiable.$$.prototype.power);
atr$(JSNum$proto, 'negated', function() {
    return nflt$(this) ? Float(-this) : -this;
},undefined,function(){return{an:function(){return[shared(),actual()]},mod:$CCMM$,$cont:Invertible,d:['$','Invertible','$at','negated']};});
atr$(JSNum$proto, 'negative', function(){
  return nflt$(this) ? this < 0.0 : this.valueOf() < 0;
},undefined,function(){return{$t:{t:$_Boolean},an:function(){return[shared(),actual()]},mod:$CCMM$,$cont:$_Number,d:['$','Number','$at','negative']};});
atr$(JSNum$proto, 'positive', function(){
  return nflt$(this) ? this > 0.0 : this.valueOf() > 0;
},undefined,function(){return{$t:{t:$_Boolean},an:function(){return[shared(),actual()]},mod:$CCMM$,$cont:$_Number,d:['$','Number','$at','positive']};});
JSNum$proto.equals = function(other) { return (typeof(other)==='number' || other.constructor===Number) && other==this.valueOf(); }
$addnm$('equals',$_Object.$$.prototype.equals);
JSNum$proto.compare = function(other) {
    var value = this.valueOf();
    return value==other ? getEqual() : (value<other ? getSmaller():getLarger());
}
$addnm$('compare',Comparable.$$.prototype.compare);
JSNum$proto.smallerThan=function(o) {
  return Comparable.$$.prototype.smallerThan.call(this,o);
}
$addnm$('smallerThan',Comparable.$$.prototype.smallerThan);
JSNum$proto.largerThan=function(o) {
  return Comparable.$$.prototype.largerThan.call(this,o);
}
$addnm$('largerThan',Comparable.$$.prototype.largerThan);
JSNum$proto.notSmallerThan=function(o) {
  return Comparable.$$.prototype.notSmallerThan.call(this,o);
}
$addnm$('notSmallerThan',Comparable.$$.prototype.notSmallerThan);
JSNum$proto.notLargerThan=function(o) {
  return Comparable.$$.prototype.notLargerThan.call(this,o);
}
$addnm$('notLargerThan',Comparable.$$.prototype.notLargerThan);
atr$(JSNum$proto, '$_float', function(){ return Float(this.valueOf()); },
  undefined,function(){return{an:function(){return[shared(),actual()]},mod:$CCMM$,$cont:$_Number,d:['$','Number','$at','float']};});
atr$(JSNum$proto, 'integer', function(){ return toInt(this); },
  undefined,function(){return{an:function(){return[shared(),actual()]},mod:$CCMM$,$cont:$_Number,d:['$','Number','$at','integer']};});
atr$(JSNum$proto, 'character', function(){ return Character(this.valueOf()); },
  undefined,function(){return{an:function(){return[shared(),actual()]},mod:$CCMM$,$cont:Integer,d:['$','Integer','$at','character']};});
atr$(JSNum$proto, 'successor', function(){ return this+1; },
  undefined,function(){return{an:function(){return[shared(),actual()]},mod:$CCMM$,$cont:Ordinal,d:['$','Ordinal','$at','successor']};});
atr$(JSNum$proto, 'predecessor', function(){ return this-1; },
  undefined,function(){return{an:function(){return[shared(),actual()]},mod:$CCMM$,$cont:Ordinal,d:['$','Ordinal','$at','predecessor']};});
atr$(JSNum$proto, 'unit', function(){ return this == 1; },
  undefined,function(){return{an:function(){return[shared(),actual()]},mod:$CCMM$,$cont:Integral,d:['$','Integral','$at','unit']};});
atr$(JSNum$proto, 'zero', function(){ return this == 0; },
  undefined,function(){return{an:function(){return[shared(),actual()]},mod:$CCMM$,$cont:Integral,d:['$','Integral','$at','zero']};});
atr$(JSNum$proto, 'fractionalPart', function() {
    if (!nflt$(this)) { return 0; }
    return Float(this - toInt(this));
},undefined,function(){return{an:function(){return[shared(),actual()]},mod:$CCMM$,$cont:$_Number,d:['$','Number','$at','fractionalPart']};});
atr$(JSNum$proto, 'wholePart', function() {
    if (!nflt$(this)) { return this.valueOf(); }
    return Float(this>=0 ? Math.floor(this) : Math.ceil(this));
},undefined,function(){return{an:function(){return[shared(),actual()]},mod:$CCMM$,$cont:$_Number,d:['$','Number','$at','wholePart']};});
atr$(JSNum$proto, 'sign', function(){ return this > 0 ? 1 : this < 0 ? -1 : 0; },
  undefined,function(){return{an:function(){return[shared(),actual()]},mod:$CCMM$,$cont:$_Number,d:['$','Number','$at','sign']};});
atr$(JSNum$proto, 'hash', function() {
    return nflt$(this) ? $_String(this.toPrecision()).hash : this.valueOf();
},undefined,function(){return{an:function(){return[shared(),actual()]},mod:$CCMM$,$cont:$_Object,d:['$','Object','$at','hash']};});
JSNum$proto.distanceFrom = function(other) {
    return (nflt$(this) ? this.wholePart : this) - other;
}
//Binary interface
atr$(JSNum$proto, 'not', function(){ return ~this; },
  undefined,function(){return{an:function(){return[shared(),actual()]},mod:$CCMM$,$cont:Binary,d:['$','Binary','$at','not']};});
JSNum$proto.leftLogicalShift = function(i) { return this << i; }
$addnm$('leftLogicalShift',Binary.$$.prototype.leftLogicalShift);
JSNum$proto.rightLogicalShift = function(i) { return this >>> i; }
$addnm$('rightLogicalShift',Binary.$$.prototype.rightLogicalShift);
JSNum$proto.rightArithmeticShift = function(i) { return this >> i; }
$addnm$('rightArithmeticShift',Binary.$$.prototype.rightArithmeticShift);
JSNum$proto.and = function(x) { return this & x; }
$addnm$('and',Binary.$$.prototype.and);
JSNum$proto.or = function(x) { return this | x; }
$addnm$('or',Binary.$$.prototype.or);
JSNum$proto.xor = function(x) { return this ^ x; }
$addnm$('xor',Binary.$$.prototype.xor);
JSNum$proto.$_get = function(idx) {
    if (idx < 0 || idx >31) {
        return false;
    } 
    var mask = 1 << idx;
    return (this & mask) != 0 ? true : false;
}
$addnm$('$_get',Binary.$$.prototype.$_get);
JSNum$proto.set = function(idx,bit) {
    if (idx < 0 || idx >31) {
        return this;
    } 
    if (bit === undefined) { bit = true; }
        var mask = idx > 1 ? 1 << idx : 1;
    return (bit === true) ? this | mask : this & ~mask;
}
$addnm$('set',Binary.$$.prototype.set);
JSNum$proto.flip = function(idx) {
    if (idx < 0 || idx >31) {
        return this;
    } 
    var mask = 1 << idx;
    return this ^ mask;
}
$addnm$('flip',Binary.$$.prototype.flip);
JSNum$proto.clear = function(index) {
    return this.set(index, false);
}
$addnm$('clear',Binary.$$.prototype.clear);
JSNum$proto.neighbour=function(offset) {
  return this+offset;
}
$addnm$('neighbour',Enumerable.$$.prototype.neighbour);
JSNum$proto.offset=function(other) {
  return this-other;
}
$addnm$('offset',Enumerable.$$.prototype.offset);
JSNum$proto.offsetSign=function(other) {
  return this-other;
}
$addnm$('offsetSign',Enumerable.$$.prototype.offsetSign);

atr$(JSNum$proto, 'magnitude', function(){ return Math.abs(this); },
  undefined,function(){return{an:function(){return[shared(),actual()]},mod:$CCMM$,$cont:$_Number,d:['$','Number','$at','magnitude']};});

atr$(JSNum$proto, '$_undefined', function(){ return isNaN(this); },
  undefined,function(){return{an:function(){return[shared(),actual()]},mod:$CCMM$,$cont:Float,d:['$','Float','$at','undefined']};});
atr$(JSNum$proto, 'finite', function(){ return this!=Infinity && this!=-Infinity && !isNaN(this); },
  undefined,function(){return{an:function(){return[shared(),actual()]},mod:$CCMM$,$cont:Float,d:['$','Float','$at','finite']};});
atr$(JSNum$proto, 'infinite', function(){ return this==Infinity || this==-Infinity; },
  undefined,function(){return{an:function(){return[shared(),actual()]},mod:$CCMM$,$cont:Float,d:['$','Float','$at','infinite']};});
atr$(JSNum$proto, 'strictlyPositive', function(){ return this>0 || (this==0 && (1/this==Infinity)); },
  undefined,function(){return{an:function(){return[shared(),actual()]},mod:$CCMM$,$cont:Float,d:['$','Float','$at','strictlyPositive']};});
atr$(JSNum$proto, 'strictlyNegative', function() { return this<0 || (this==0 && (1/this==-Infinity)); },
  undefined,function(){return{an:function(){return[shared(),actual()]},mod:$CCMM$,$cont:Float,d:['$','Float','$at','strictlyNegative']};});

var $infinity = Float(Infinity);
function getInfinity() { return $infinity; }
ex$.$prop$getInfinity={get:getInfinity,$crtmm$:function(){return{mod:$CCMM$,$t:{t:Float},d:['$','infinity']};}};

ex$.Integer=Integer;
ex$.Float=Float;
ex$.getInfinity=getInfinity;
