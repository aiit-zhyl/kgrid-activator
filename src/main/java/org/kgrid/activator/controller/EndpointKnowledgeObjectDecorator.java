package org.kgrid.activator.controller;

import javax.servlet.http.HttpServletRequest;
import org.kgrid.activator.services.ActivationService;
import org.kgrid.activator.services.ServiceDescriptionService;
import org.kgrid.shelf.controller.KnowledgeObjectDecorator;
import org.kgrid.shelf.domain.KnowledgeObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
class EndpointKnowledgeObjectDecorator implements KnowledgeObjectDecorator {

  @Autowired
  private ActivationService activationService;

  @Autowired
  private ServiceDescriptionService serviceDescriptionService;

  @Override
  public void decorate(KnowledgeObject knowledgeObject, HttpServletRequest httpServletRequest) {

    if (activationService.getEndpoints().containsKey(activationService.getKnowleledgeObjectPath(
        knowledgeObject)+ activationService.getEndPointPath(knowledgeObject))) {

      knowledgeObject.getMetadata().put("endpoint",
          httpServletRequest.getRequestURL() + activationService.getEndPointPath(
              knowledgeObject));
    }
  }
}