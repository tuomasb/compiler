int getOne() {
  return 1;
}

main {
  int s;
  int[] array;
  array := new int [3];
  array[0] := 0;
  array[1] := 1;
  array[2] := 2;
  s := getOne(array);
  return s;
}

