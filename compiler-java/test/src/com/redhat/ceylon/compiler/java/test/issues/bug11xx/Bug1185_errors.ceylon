void bug1185_errors() {
    variable Integer x;
    x = 9223372036854775808;
    x = -9223372036854775809;
    x = #1_00_00_00_00_00_00_00_00;
    x = $1_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000;
}