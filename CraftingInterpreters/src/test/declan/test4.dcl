(* Hofstadter Male-Female Sequences: https://mathworld.wolfram.com/HofstadterMale-FemaleSequences.html *)
VAR n, i, result: INTEGER;
PROCEDURE M(n: INTEGER; VAR result: INTEGER);
VAR temp: INTEGER;
BEGIN
  IF n = 0
  THEN result := 0
  ELSE
    M(n - 1, temp);
    F(temp, result);
    result := n - result
  END
END M;
PROCEDURE F(n: INTEGER; VAR result: INTEGER);
VAR temp: INTEGER;
BEGIN
  IF n = 0
  THEN result := 1
  ELSE
    F(n - 1, temp);
    M(temp, result);
    result := n - result
  END
END F;
PROCEDURE Fact(n: INTEGER; VAR result: INTEGER);
VAR temp: INTEGER;
BEGIN
  IF n = 0
  THEN result := 1
  ELSE
    Fact(n - 1, temp);
    result := n * temp
  END
END Fact;
BEGIN
  ReadInt(n);
  FOR i := 0 TO n DO
    F(i, result);
    WriteInt(result);
    Fact(i, result);
    WriteInt(result);
    WriteLn()
  END
END.