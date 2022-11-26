CONST six = 6; seven = 7;
VAR answer : INTEGER;
PROCEDURE gcd(a, b: INTEGER; VAR result: INTEGER);
  BEGIN
    WHILE a # b DO
      IF a > b THEN a := a - b ELSE b := b - a END
    END;
    result := a
  END gcd;
BEGIN
  gcd(six, seven, answer);
  answer := six * seven * answer;
  WriteReal(answer * 1.);
  WriteLn()
END. (* Don't forget the ending period! *)