main {
  boolean a;
  a :=  ! false && 6 < 3 && 3 < 4;
  if (a) then
  {
    print(1);
  }
  else
  {
    print(0);
  }
  fi
  return a;
}

