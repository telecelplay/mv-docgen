package org.meveo.doc;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.technicalservice.endpoint.Endpoint;
import org.meveo.service.technicalservice.endpoint.EndpointService;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.technicalservice.endpoint.TSParameterMapping;
import org.meveo.model.module.MeveoModule;
import org.meveo.model.module.MeveoModuleItem;
import org.meveo.model.BusinessEntity;
import org.meveo.security.MeveoUser;
import org.meveo.service.admin.impl.MeveoModuleService;
import org.meveo.service.crm.impl.CurrentUserProducer;
import org.meveo.service.script.Script;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocGenScript extends Script {
	private static final Logger log = LoggerFactory.getLogger(DocGenScript.class);
  	private static final String CET_CLASS = CustomEntityTemplate.class.getName();
  	private	static final String ENDPOINT_CLASS = Endpoint.class.getName();
  	private static final String SCRIPT_INSTANCE_CLASS = ScriptInstance.class.getName();
  
  	private MeveoModuleService meveoModuleService = getCDIBean(MeveoModuleService.class);
	private ParamBeanFactory paramBeanFactory = getCDIBean(ParamBeanFactory.class);
	private CurrentUserProducer currentUserProducer = getCDIBean(CurrentUserProducer.class);
  	private EndpointService endpointService = getCDIBean(EndpointService.class);

	private String moduleCode;
	private Object result;

	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}

	public Object getResult() {
		return result;
	}
  
	@Override
	public void execute(Map<String, Object> parameters) throws BusinessException {
		super.execute(parameters);
		log.info("moduleCode: {}", moduleCode);
		if (moduleCode == null) {
			throw new BusinessException("moduleCode not set");
		}
		MeveoModule module = meveoModuleService.findByCode(moduleCode);
		MeveoUser user = currentUserProducer.getCurrentUser();
		ParamBean appConfig = paramBeanFactory.getInstance();

		log.info("user: {}", user);

		if (module != null) {
			log.info("Module found: {}", module.getCode());
			Set<MeveoModuleItem> moduleItems = module.getModuleItems();
			moduleItems.stream().forEach(m -> log.info("module item code == {}, item class == {}",m.getItemCode(),m.getItemClass()));

          	List<String> endpointCodes = moduleItems.stream()
					.filter(item -> ENDPOINT_CLASS.equals(item.getItemClass()))
					.map(entity -> entity.getItemCode())
					.collect(Collectors.toList());
            log.info("endpointCodes == {}",endpointCodes);
			endpointCodes.forEach(c -> {
              	log.info("endpoint code == {}",c);
              	Endpoint endpoint = endpointService.findByCode(c);
              	if(endpoint == null){
                  log.info("endpoint not found");
                }
              	log.info("endpoint method == {}, content-type == {}, url == {}, ",endpoint.getMethod().getLabel(),endpoint.getContentType(),endpoint.getEndpointUrl());
              	log.info("total endpoint input fields size == {}",endpoint.getParametersMapping().size());
            	endpoint.getParametersMapping().forEach(f -> {
                  log.info("field name == {}",f.getParameterName());
                });
            });
         
          	List<String> scriptInstanceCodes = moduleItems.stream()
					.filter(item -> SCRIPT_INSTANCE_CLASS.equals(item.getItemClass()))
					.map(entity -> entity.getItemCode())
					.collect(Collectors.toList());
			scriptInstanceCodes.forEach(c -> {
              	log.info("scriptInstance code == {}",c);
              	Endpoint endpoint = endpointService.findByCode(c);
              	if(endpoint == null){
                  log.info("endpoint not found");
                }
              	log.info("endpoint method == {}, content-type == {}, url == {}, ",endpoint.getMethod().getLabel(),endpoint.getContentType(),endpoint.getEndpointUrl());
              	log.info("total endpoint input fields size == {}",endpoint.getParametersMapping().size());
            	endpoint.getParametersMapping().forEach(f -> {
                  log.info("field name == {}",f.getParameterName());
                });
            });
          
          	List<String> entityCodes = moduleItems.stream()
					.filter(item -> CET_CLASS.equals(item.getItemClass()))
					.map(entity -> entity.getItemCode())
					.collect(Collectors.toList());
			log.info("entityCodes: {}", entityCodes);
        }
	}	
}
