CONST a = 0; b = 1.2; c = -3.14 + a; d = 6 * (6 + 1); e = a * b # c / d;
VAR x, y, z: REAL;
  l, m, n: INTEGER;
  p, q, r: BOOLEAN;
BEGIN
  x := b / b;
  l := d MOD 10;
  y := -c - b*a;
  WriteInt(a);
  WriteReal(x);
  WriteReal(l);
  WriteReal(y);
  WriteLn();
  m := d DIV l;
  z := d / l;
  WriteInt(m);
  WriteReal(z);
  WriteLn();
  Round(c, n);
  WriteInt(n);
  IF e THEN WriteInt(2 * 2) ELSE WriteReal(d / 10) END;
  WriteLn();
  p := ~e & (x > a);
  q := ~e OR (x >= a);
  r := p = q;
  IF p THEN WriteInt(4) ELSIF q THEN WriteInt(5) END;
  IF r THEN WriteInt(5) ELSE WriteInt(6) END;
  WriteLn()
END.