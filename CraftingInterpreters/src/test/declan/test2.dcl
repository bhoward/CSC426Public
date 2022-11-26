(* This is a short program that tests some of the features of
the DeCLan subset of the Oberon programming language *)
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
  WHILE n < 0 DO n := -n;
  ELSIF n > 0 DO Display(n)
  END;
  FOR n := 10 TO 1 BY -1 DO WriteInt(n) END;
  REPEAT Display(n) UNTIL TRUE
END. (* Don't forget the ending period *)