main {
  int a;
  a := 0;
  repeat
  {
    print(a);
    a := a + 1;
  }
  until(!(a < 6));

  return a;
}

