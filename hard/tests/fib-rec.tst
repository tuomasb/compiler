int fib(int n) {
  int ret;
  if (2 < n) then
  {
    ret := fib(n - 1) + fib(n - 2);
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

