#!/bin/bash

curl -X POST -H "Content-Type: application/json" -d @hello.json http://localhost:8080/api/hello/message
