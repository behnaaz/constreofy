# extendedcoloring

To be viewed in raw format to eradableversion
Basic Constraints:

Example 1 =========================================================
(p1 <=> p2)
(p1 <=> p2) and (e <=> c or d) and (not (c and d))
(p1 <=> p2) and (e <=> c or d) and (not (c and d)) and (f <=> g)
(p1 <=> p2) and (e <=> c or d) and (not (c and d)) and (f <=> g) and (j <=> h or i) and (not (h and i))

Example 2 =========================================================
(a1 <=> a2 or a3) and (not (a2 and a3))
(a1 <=> a2 or a3) and (not (a2 and a3)) and (not b1)
(a1 <=> a2 or a3) and (not (a2 and a3)) and (not b1) and (c1 <=> c2 or c3) and (not (c2 and c3))
(a1 <=> a2 or a3) and (not (a2 and a3)) and (not b1) and (c1 <=> c2 or c3) and (not (c2 and c3)) and (p1 <=> p2)
(a1 <=> a2 or a3) and (not (a2 and a3)) and (not b1) and (c1 <=> c2 or c3) and (not (c2 and c3)) and (p1 <=> p2) and (not (p1 and p2))
(a1 <=> a2 or a3) and (not (a2 and a3)) and (not b1) and (c1 <=> c2 or c3) and (not (c2 and c3)) and (p1 <=> p2) and (not (p1 and p2)) and (f2 <=> f1 or f3) and (not (f1 and f3))
(a1 <=> a2 or a3) and (not (a2 and a3)) and (not b1) and (c1 <=> c2 or c3) and (not (c2 and c3)) and (p1 <=> p2) and (not (p1 and p2)) and (f2 <=> f1 or f3) and (not (f1 and f3)) and (p2 => p1)
(a1 <=> a2 or a3) and (not (a2 and a3)) and (not b1) and (c1 <=> c2 or c3) and (not (c2 and c3)) and (p1 <=> p2) and (not (p1 and p2)) and (f2 <=> f1 or f3) and (not (f1 and f3)) and (p2 => p1) and (not (p1 and p2))
(a1 <=> a2 or a3) and (not (a2 and a3)) and (not b1) and (c1 <=> c2 or c3) and (not (c2 and c3)) and (p1 <=> p2) and (not (p1 and p2)) and (f2 <=> f1 or f3) and (not (f1 and f3)) and (p2 => p1) and (not (p1 and p2)) and (p1 <=> p2)
(a1 <=> a2 or a3) and (not (a2 and a3)) and (not b1) and (c1 <=> c2 or c3) and (not (c2 and c3)) and (p1 <=> p2) and (not (p1 and p2)) and (f2 <=> f1 or f3) and (not (f1 and f3)) and (p2 => p1) and (not (p1 and p2)) and (p1 <=> p2) and  (i2 == a3) 


Priority:
Example 1 =========================================================
Nodes : 2 Lines : 1
 a   b
  .  .
Connecting c to b
Nodes : 5 Lines : 3
  a  b  c  d  e
  .  .  o  x  .
  .  .  o  o  .
  .  .  .  .  o
Connecting f to a
Nodes : 7 Lines : 6
  a  b  c  d  e  f  g
  .  .  o  x  .  o  .
  .  .  o  x  .  .  o
  .  .  o  o  .  o  .
  .  .  o  o  .  .  o
  .  .  .  .  o  o  .
  .  .  .  .  o  .  o
Connecting h to g
Nodes : 10 Lines : 12
  a  b  c  d  e  f  g  h  i  j
  .  .  o  x  .  o  .  o  x  .
  .  .  o  x  .  o  .  o  o  .
  .  .  o  x  .  o  .  .  .  o
  .  .  o  x  .  .  o  .  .  o
  .  .  o  o  .  o  .  o  x  .
  .  .  o  o  .  o  .  o  o  .
  .  .  o  o  .  o  .  .  .  o
  .  .  o  o  .  .  o  .  .  o
  .  .  .  .  o  o  .  o  x  .
  .  .  .  .  o  o  .  o  o  .
  .  .  .  .  o  o  .  .  .  o
  .  .  .  .  o  .  o  .  .  o
Nodes : 10 Lines : 1
  a  b  c  d  e  f  g  h  i  j
  .  .  o  x  .  o  .  o  x  .

Example 2 =========================================================
Nodes : 3 Lines : 4
  a1  a2  a3
   x   x   x
   .   o   o
   .   o   x
   .   x   o
Connecting b1 to a2
Nodes : 5 Lines : 4
  a1  a2  a3  b1  b2
   x   x   x   x   x
   x   x   x   x   o
   .   x   o   x   x
   .   x   o   x   o
Connecting c1 to b2
Nodes : 8 Lines : 8
  a1  a2  a3  b1  b2  c1  c2  c3
   x   x   x   x   x   x   x   x
   x   x   x   x   o   .   o   o
   x   x   x   x   o   .   o   x
   x   x   x   x   o   .   x   o
   .   x   o   x   x   x   x   x
   .   x   o   x   o   .   o   o
   .   x   o   x   o   .   o   x
   .   x   o   x   o   .   x   o
Connecting d1 to c3
Nodes : 10 Lines : 4
  a1  a2  a3  b1  b2  c1  c2  c3  d1  d2
   x   x   x   x   o   .   o   o   .   .
   x   x   x   x   o   .   x   o   .   .
   .   x   o   x   o   .   o   o   .   .
   .   x   o   x   o   .   x   o   .   .
Connecting e1 to d2
Nodes : 12 Lines : 8
  a1  a2  a3  b1  b2  c1  c2  c3  d1  d2  e1  e2
   x   x   x   x   o   .   o   o   .   .   o   .
   x   x   x   x   o   .   o   o   .   .   .   o
   x   x   x   x   o   .   x   o   .   .   o   .
   x   x   x   x   o   .   x   o   .   .   .   o
   .   x   o   x   o   .   o   o   .   .   o   .
   .   x   o   x   o   .   o   o   .   .   .   o
   .   x   o   x   o   .   x   o   .   .   o   .
   .   x   o   x   o   .   x   o   .   .   .   o
Connecting f1 to e2
Nodes : 15 Lines : 8
  a1  a2  a3  b1  b2  c1  c2  c3  d1  d2  e1  e2  f2  f1  f3
   x   x   x   x   o   .   o   o   .   .   o   .   .   o   o
   x   x   x   x   o   .   o   o   .   .   o   .   .   o   x
   x   x   x   x   o   .   x   o   .   .   o   .   .   o   o
   x   x   x   x   o   .   x   o   .   .   o   .   .   o   x
   .   x   o   x   o   .   o   o   .   .   o   .   .   o   o
   .   x   o   x   o   .   o   o   .   .   o   .   .   o   x
   .   x   o   x   o   .   x   o   .   .   o   .   .   o   o
   .   x   o   x   o   .   x   o   .   .   o   .   .   o   x
Connecting g1 to f2
Nodes : 17 Lines : 16
  a1  a2  a3  b1  b2  c1  c2  c3  d1  d2  e1  e2  f2  f1  f3  g2  g1
   x   x   x   x   o   .   o   o   .   .   o   .   .   o   o   o   .
   x   x   x   x   o   .   o   o   .   .   o   .   .   o   o   .   o
   x   x   x   x   o   .   o   o   .   .   o   .   .   o   x   o   .
   x   x   x   x   o   .   o   o   .   .   o   .   .   o   x   .   o
   x   x   x   x   o   .   x   o   .   .   o   .   .   o   o   o   .
   x   x   x   x   o   .   x   o   .   .   o   .   .   o   o   .   o
   x   x   x   x   o   .   x   o   .   .   o   .   .   o   x   o   .
   x   x   x   x   o   .   x   o   .   .   o   .   .   o   x   .   o
   .   x   o   x   o   .   o   o   .   .   o   .   .   o   o   o   .
   .   x   o   x   o   .   o   o   .   .   o   .   .   o   o   .   o
   .   x   o   x   o   .   o   o   .   .   o   .   .   o   x   o   .
   .   x   o   x   o   .   o   o   .   .   o   .   .   o   x   .   o
   .   x   o   x   o   .   x   o   .   .   o   .   .   o   o   o   .
   .   x   o   x   o   .   x   o   .   .   o   .   .   o   o   .   o
   .   x   o   x   o   .   x   o   .   .   o   .   .   o   x   o   .
   .   x   o   x   o   .   x   o   .   .   o   .   .   o   x   .   o
Connecting h1 to f3
Nodes : 19 Lines : 16
  a1  a2  a3  b1  b2  c1  c2  c3  d1  d2  e1  e2  f2  f1  f3  g2  g1  h1  h2
   x   x   x   x   o   .   o   o   .   .   o   .   .   o   o   o   .   .   o
   x   x   x   x   o   .   o   o   .   .   o   .   .   o   o   .   o   .   o
   x   x   x   x   o   .   o   o   .   .   o   .   .   o   x   o   .   x   x
   x   x   x   x   o   .   o   o   .   .   o   .   .   o   x   .   o   x   x
   x   x   x   x   o   .   x   o   .   .   o   .   .   o   o   o   .   .   o
   x   x   x   x   o   .   x   o   .   .   o   .   .   o   o   .   o   .   o
   x   x   x   x   o   .   x   o   .   .   o   .   .   o   x   o   .   x   x
   x   x   x   x   o   .   x   o   .   .   o   .   .   o   x   .   o   x   x
   .   x   o   x   o   .   o   o   .   .   o   .   .   o   o   o   .   .   o
   .   x   o   x   o   .   o   o   .   .   o   .   .   o   o   .   o   .   o
   .   x   o   x   o   .   o   o   .   .   o   .   .   o   x   o   .   x   x
   .   x   o   x   o   .   o   o   .   .   o   .   .   o   x   .   o   x   x
   .   x   o   x   o   .   x   o   .   .   o   .   .   o   o   o   .   .   o
   .   x   o   x   o   .   x   o   .   .   o   .   .   o   o   .   o   .   o
   .   x   o   x   o   .   x   o   .   .   o   .   .   o   x   o   .   x   x
   .   x   o   x   o   .   x   o   .   .   o   .   .   o   x   .   o   x   x
Connecting i1 to h2
Nodes : 21 Lines : 8
  a1  a2  a3  b1  b2  c1  c2  c3  d1  d2  e1  e2  f2  f1  f3  g2  g1  h1  h2  i2  i1
   x   x   x   x   o   .   o   o   .   .   o   .   .   o   o   o   .   .   o   .   .
   x   x   x   x   o   .   o   o   .   .   o   .   .   o   o   .   o   .   o   .   .
   x   x   x   x   o   .   x   o   .   .   o   .   .   o   o   o   .   .   o   .   .
   x   x   x   x   o   .   x   o   .   .   o   .   .   o   o   .   o   .   o   .   .
   .   x   o   x   o   .   o   o   .   .   o   .   .   o   o   o   .   .   o   .   .
   .   x   o   x   o   .   o   o   .   .   o   .   .   o   o   .   o   .   o   .   .
   .   x   o   x   o   .   x   o   .   .   o   .   .   o   o   o   .   .   o   .   .
   .   x   o   x   o   .   x   o   .   .   o   .   .   o   o   .   o   .   o   .   .
Nodes : 21 Lines : 1
  a1  a2  a3  b1  b2  c1  c2  c3  d1  d2  e1  e2  f2  f1  f3  g2  g1  h1  h2  i2  i1
   .   x   o   x   o   .   x   o   .   .   o   .   .   o   o   .   o   .   o   .   .
