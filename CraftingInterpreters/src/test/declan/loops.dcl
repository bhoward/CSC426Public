VAR i: INTEGER;
BEGIN
  FOR i := 1 TO 10 DO WriteInt(i) END;
  WriteLn();
  FOR i := 1 TO 10 BY 2 DO WriteInt(i) END;
  WriteLn();
  FOR i := 10 TO 1 BY -2 DO WriteInt(i) END;
  WriteLn();
  FOR i := 10 TO 1 DO WriteInt(i) END;
  WriteLn();
  
  i := 1;
  WHILE i <= 10 DO WriteInt(i); i := i + 1 END;
  WriteLn();
  i := 1;
  WHILE (i <= 10) & (i MOD 2 = 1) DO WriteInt(i); i := i + 1
  ELSIF i <= 10 DO i := i + 1 END;
  WriteLn();
  i := 10;
  REPEAT WriteInt(i); i := i - 2 UNTIL i < 1;
  WriteLn();
  i := 10;
  REPEAT WriteInt(i); i := i + 1 UNTIL i >= 1;
  WriteLn();
END.