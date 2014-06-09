main {
  int a;
  int b;
  int[] array1;
  int[] array2;
  a := 0;
  b := 0;
  array1 := new int [3];
  array2 := new int [3];
  array1[0] := 0;
  array1[1] := 10;
  array1[2] := 100;
  array2[0] := 1;
  array2[1] := 11;
  array2[2] := 101;
  repeat
  {
    repeat
    {
      if (b < 1) then
      {
        print(array1[a]);
      }
      else
      {
        print(array2[a]);
      }
      fi
      b := b + 1;
    }
    until(!(b < 2));

    b := 0;
    a := a + 1;
  }
  until(!(a < 3));

  return 0;
}

