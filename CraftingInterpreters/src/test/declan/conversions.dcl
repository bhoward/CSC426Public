VAR x : REAL; n : INTEGER;
PROCEDURE p(n: INTEGER; x: REAL; VAR r: REAL);
  VAR m : INTEGER;
  BEGIN
    Round(x + n, m);
    r := r + m;
  END p;
BEGIN
  n := 1;
  x := 2;
  WriteInt(n);
  WriteReal(n);
  WriteReal(x);
  WriteLn();
  WriteReal(n / x);
  WriteInt((n + 5) * (n + 6));
  WriteReal((x + 4) * (x + 5.));
  WriteLn();
  p(n, 3.1415, x);
  WriteReal(x);
  WriteLn();
END.