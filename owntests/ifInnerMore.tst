main {
  int a;
  int ret;
  a := 6;
  if ((3 < a) && (4 < a)) then
  {
    if (5 < a) then
      if(true) then
        if(true) then
          if(true) then
            if(true) then
              {
              print(100);
              ret := 5000;
              }
            else { }
            fi
          else { }
          fi
        else { }
        fi
      else { }
      fi
    else {
      ret := 0;
    }
    fi
  }
  else
  {
    ret := 0 - 1;
  }
  fi
  print(ret);
  return ret;
}

