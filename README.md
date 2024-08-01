# README

- Install [sbt](https://www.scala-sbt.org/1.x/docs/Setup.html) (Scala Build Tool)
- Compile `sbt clean compile`
- Test `sbt test`  
- Run `sbt run`

```parent(B) = A 
parent(C) = A
parent(D) = B
parent(B) = C
sum(A)    => f(A) + sum(children) where f is amount at any given time
          = f(A) + sum(B,C)
          = f(A) + {f(B) + sum(D)} + {f(C)+sum(B)}
```
 sum(A)   = f(A) + f(B) + f(D) + f(C) + ~f(B)~ + ~f(D)~
```
sum(B)    = f(B) + sum(D)
          = f(B) + f(D)
sum(C)    = f(C) + sum(B)
          = f(C) + f(B) + f(D)
sum(D)    = f(D)
```

Example commands (GET, POST, PUT):

- `POST/PUT`
curl  -H "Content-Type: application/json" -X PUT http://localhost:8080/transactionservice/transaction/10 -d '{ "type":"cars","amount" : 5000}'


- `POST/PUT`
curl  -H "Content-Type: application/json" -X PUT http://localhost:8080/transactionservice/transaction/11 -d '{ "type":"shopping","amount" : 10000, "parent_id":10}'


- `GET`
curl http://localhost:8080/transactionservice/types/cars


- `GET`
curl  http://localhost:8080/transactionservice/sum/10


- `GET`
curl  http://localhost:8080/transactionservice/sum/11  
