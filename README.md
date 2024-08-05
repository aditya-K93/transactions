# README

- Install [sbt](https://www.scala-sbt.org/1.x/docs/Setup.html) (Scala Build Tool)
- Compile `sbt clean compile`
- Test `sbt test`  
- Run `sbt run`

Transitive Sum (recursive definition)
```parent(B) = A 
parent(C) = A
parent(D) = B
parent(B) = C
for any given txn x: transitive_sum(x)   => f(x) + transitive_sum(children(x)) where f(x) is cost of txn x
transitive_sum(A)    = f(A) + transitive_sum(C)
transitive_sum(B)    = f(B) + transitive_sum(D)
transitive_sum(C)    = f(C) + transitive_sum(B)
transitive_sum(D)    = f(D)

transitive_sum(B)    = f(B) + transitive_sum(D)
                     = f(B) + f(D)
transitive_sum(C)    = f(C) + transitive_sum(B)
                     = f(C) + f(B) + f(D)
transitive_sum(A)    = f(A) + transitive_sum(C)                    
                     = f(A) + f(C) + f(B) + f(D)
```

Example commands (GET, POST, PUT):

- `POST/PUT`
curl -v  -H "Content-Type: application/json" -X PUT http://localhost:8080/transactionservice/transaction/10 -d '{ "type":"cars","amount" : 5000}'


- `POST/PUT`
curl -v  -H "Content-Type: application/json" -X PUT http://localhost:8080/transactionservice/transaction/11 -d '{ "type":"shopping","amount" : 10000, "parent_id":10}'


- `GET`
curl -v http://localhost:8080/transactionservice/types/cars


- `GET`
curl -v  http://localhost:8080/transactionservice/sum/10


- `GET`
curl -v  http://localhost:8080/transactionservice/sum/11
