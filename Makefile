APP_NAME := api-core-ejercito-statistics-gateway-microservice
#DOCKER_REGISTRY := 10.64.170.11:8082
DOCKER_REGISTRY := dbacilio88
DOCKER_IMAGE := $(DOCKER_REGISTRY)/$(APP_NAME)
GIT_COMMIT := $(shell git rev-parse --short HEAD)
VERSION := $(shell mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
DOCKER_TAG := $(GIT_COMMIT)

all: release

build:
	@echo "Clean and build jar $(APP_NAME):$(VERSION)"
	mvn clean compile package

docker_build:
	@echo "build image  $(DOCKER_IMAGE):$(DOCKER_TAG)"
	@docker image build -t $(DOCKER_IMAGE):$(DOCKER_TAG) .

docker_tag:
	@echo "tag image  $(DOCKER_IMAGE):$(DOCKER_IMAGE) to $(DOCKER_IMAGE):latest"
	@docker tag $(DOCKER_IMAGE):$(DOCKER_TAG) $(DOCKER_IMAGE):latest

docker_push:
	@echo "push image latest $(DOCKER_IMAGE):latest"
	@docker push $(DOCKER_IMAGE):latest
	@echo "push image current $(DOCKER_IMAGE):$(DOCKER_TAG)"
	@docker push $(DOCKER_IMAGE):$(DOCKER_TAG)

docker_clean:
	@echo "remove image $(DOCKER_IMAGE):latest"
	@docker rmi $(DOCKER_IMAGE):latest
	@echo "remove image $(DOCKER_IMAGE):$(DOCKER_TAG)"
	@docker rmi $(DOCKER_IMAGE):$(DOCKER_TAG)

release: build docker_build docker_tag docker_push docker_clean

.PHONY: build docker_build docker_tag docker_push docker_clean