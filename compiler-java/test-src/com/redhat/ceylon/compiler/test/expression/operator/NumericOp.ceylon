@nomodel
class NumericOp(){
    shared Natural m() {
        variable Natural i := 1;
        i++;
        ++i;
        i--;
        --i;
        i += 1;
        i -= 1;
        i *= 1;
        i /= 1;
        i %= 100;
        i := +i;
        i := -i;
        return 2**3 * 1 / 1 % 100 + 1 - 1;
    }
}