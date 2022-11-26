CONST a = 42;
  b = 42.0;
VAR c, d: INTEGER;
  e, f: REAL;
  g: BOOLEAN;
PROCEDURE Foo(VAR arg1: INTEGER; arg2, arg3: REAL; VAR arg4: BOOLEAN);
  CONST h = a = b;
    i = 355 / 113;
  VAR j: INTEGER;
  BEGIN
    FOR j := a TO arg1 BY -20 DO
      Bar();
    END;
    arg4 := h & (i > 3.14159265);
  END Foo;
PROCEDURE Bar();
  VAR k: BOOLEAN;
  BEGIN
    c := c + 1;
    IF ~g THEN g := TRUE; Foo(d, 0, 0, k) END
  END Bar;
BEGIN
  Foo(c, e, a * f, g);
  IF g THEN WriteInt(c) ELSE WriteReal(b - a) END
END.