# DataStax Query Builder	

## About

This github covers using DataStax Query Builder to do basic CRUD actions

## Prerequisites

* Basic understanding of Docker images and containers. 

* Docker installed on your local system, see [Docker Installation Instructions](https://docs.docker.com/engine/installation/). 

* When using Docker for Mac or Docker for Windows, the default resources allocated to the linux VM running docker are 2GB RAM and 2 CPU's. Make sure to adjust these resources to meet the resource requirements for the containers you will be running. More information can be found here on adjusting the resources allocated to docker.

[Docker for mac](https://docs.docker.com/docker-for-mac/#advanced)

[Docker for windows](https://docs.docker.com/docker-for-windows/#advanced)


## Getting Started
1. Prepare Docker environment-see the Prerequisites section above...
2. Pull this github into a directory  
```bash
git clone https://github.com/jphaugla/datastaxQueryBuilder.git
```
3. Refer to the notes from DataStax Docker github for background on the needed DataStax images.  Directions are here:  [https://github.com/datastax/docker-images/#datastax-platform-overview](https://github.com/datastax/docker-images/#datastax-platform-overview).  Don't get too bogged down here as the included docker-compose.yaml handles most everything.
4. Open terminal and change to the github home where you will see the docker-compose.yml file, then: 
```bash
docker-compose up -d
```
5. Verify DataStax is working (may take a minute for datastax cassandra to startup so be patient)
```bash
docker exec dse cqlsh -e "desc keyspaces"
```
6. At this point can use the command shell from cassandra using
```bash
docker exec -it dse cqlsh
```
## Conclusion
At this point, run throught the cql worksheet and enjoy playing with Cassandra!

##  Additional Notes/tips

* To get the IP address of the DataStax and openldap:

```bash
export DSE_IP=$(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' dse)
```
   and:
```bash
export LDAP_IP=$(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' openldap)
```
* use `echo $DSE_IPE` and `echo $LDAP_IP` to view
* can add to the docker compose to run additional datastax workloads such as analtyics (-k), search (-s), and graph (-g)
```bash
 command:
     -s
     -g
     -k
```
