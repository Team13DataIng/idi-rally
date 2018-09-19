# idi-rally
IDI-rally

LØSNINGER:
=========

Løsning A:
----------
Start
while rundeTeller < antRunder
  while svingTeller < x
    while !SjekkSvart()
      Kjor()
    end
    while SjekkSvart()
      KjorOgSving()
    end
    svingTeller++
    if svingTeller == antSvingPerRunde
      svingTeller = 0
      rundeTeller++
    end
  end
end

Løsning B:
----------
Start
int graderA = x
int graderB = y
int graderC = z
osv.
while rundeTeller < antRunder
  while svingTeller < antSvingPerRunde
    while !SjekkSvart()
      kjor()
    end
    if svingTeller == 0
      Sving(x)
    else if svingTeller == 1
      Sving(y)
    else if svingTeller == 2
      Sving(z)
    end
    svingTeller++
    if svingTeller = antSvingPerRunde
      svingTeller = 0
      rundeTeller++
    end
  end
end
