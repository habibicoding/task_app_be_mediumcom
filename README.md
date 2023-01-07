### Setting up local dev database:

docker network create postgres_db



docker run --name postgres_db -p 5432:5432 --network=postgres_db -v "$PWD:/var/lib/postgresql/data" -e POSTGRES_PASSWORD=postgres -d postgres:alpine



docker run -it --rm --network=postgres_db postgres:alpine psql -h postgres_db -U postgres


### YouTube series link:

https://www.youtube.com/watch?v=ZKMGMZqnmOk&list=PLjuEK3Ez60n2dTFL7-KETl1yl04kOo-rM


### Part on medium.com :

https://medium.com/@habibicoding/kotlin-spring-boot-tutorial-part-1-creating-rest-endpoints-for-a-task-app-148e4aaa7d0f
