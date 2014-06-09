main {
  int l;
  int r;
  int i;
  int j;
  int k;
  int[] array;
  boolean changed;
  boolean flag;
  array := new int [10];
  array[0] := 123;
  array[1] := 12;
  array[2] := 13;
  array[3] := 23;
  array[4] := 3;
  array[5] := 0 - 3;
  array[6] := 412;
  array[7] := 12345;
  array[8] := 2;
  array[9] := 0;
  repeat
  {
    changed := false;
    i := 0 - 1;
    j := 0;
    repeat
    {
      i := j;
      j := j + 1;
      flag := j < array.length;
      if (flag) then
      {
        flag := array[i] < array[j];
      }
      else
      {
      }
      fi
    }
    until(!(flag));

    if (j < array.length) then
    {
      if (array[i] < array[j]) then
      {
      }
      else
      {
        k := array[i];
        array[i] := array[j];
        array[j] := k;
        changed := true;
      }
      fi
    }
    else
    {
    }
    fi
  }
  until(!(changed));

  j := 0;
  repeat
  {
    print(array[j]);
    j := j + 1;
  }
  until(!(j < array.length));

  return 0;
}

