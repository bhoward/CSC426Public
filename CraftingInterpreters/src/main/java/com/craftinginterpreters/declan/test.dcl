CONST answer = -42;
VAR n: INTEGER;
PROCEDURE Display(VAR arg: INTEGER);
  VAR answer: BOOLEAN;
  BEGIN
    answer := arg MOD 2 # 0;
    IF answer THEN WriteInt(1); arg := (arg - 1) DIV 2
    ELSIF arg = 0 THEN WriteInt(-1)
    ELSE WriteInt(0); arg := arg DIV 2
    END
  END Display;
BEGIN
  n := answer;
  IF n < 0 THEN n := -n;
  ELSIF n > 0 THEN Display(n)
  END
END.