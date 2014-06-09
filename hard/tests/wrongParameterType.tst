int successor(int x) {
  return x + 1;
}

main {
  int s;
  int[] array;
  array := new int [3];
  array[0] := 0;
  array[1] := 1;
  array[2] := 2;
  s := successor(array);
  return s;
}

