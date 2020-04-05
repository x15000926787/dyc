function multiply()
{
    return arguments[0][0]*arguments[0][1];
}
function max(a,b)
{

    return a>b?a:b;
}

function tmax()
{
    var s = 0;

    for (var i = 0; i < arguments[0].length; i++) {
        s = max(s,arguments[0][i]);
    }

    return s;
}
function arrsum() {
    var s = 0;

    for (var i = 0; i < arguments[0].length; i++) {
        s = s + arguments[0][i];
    }
    return s;
}
