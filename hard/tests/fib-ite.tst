int fib(int n) {
  int i1;
  int i2;
  int k;
  int ret;
  if (2 < n) then
  {
    i1 := 1;
    i2 := 1;
    k := 2;
    repeat
    {
      ret := i1 + i2;
      i1 := i2;
      i2 := ret;
      k := k + 1;
    }
    until(!(k < n));

  }
  else
  {
    ret := 1;
  }
  fi
  return ret;
}

main {
  int result;
  int n;
  int count;
  count := 0;
  n := 15;
  repeat
  {
    result := fib(count + 1);
    print(result);
    count := count + 1;
  }
  until(!(count < n));

  return result;
}

