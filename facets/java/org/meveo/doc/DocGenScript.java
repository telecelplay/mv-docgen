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
import org.meveo.model.module.MeveoModule;
import org.meveo.model.module.MeveoModuleItem;
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
  
  	private MeveoModuleService meveoModuleService = getCDIBean(MeveoModuleService.class);
	private ParamBeanFactory paramBeanFactory = getCDIBean(ParamBeanFactory.class);
	private CurrentUserProducer currentUserProducer = getCDIBean(CurrentUserProducer.class);

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

          	List<Endpoint> endpointEntities = moduleItems.stream().filter(item -> ENDPOINT_CLASS.equals(item.getItemClass()))
              .map(m -> (Endpoint)m.getItemEntity()).collect(Collectors.toList());
            log.info("endpoint size == {}",endpointEntities.size());
          	endpointEntities.forEach(m -> log.info("endpoint method=={}, content-type=={}, path=={}",m.getMethod().getLabel(),m.getContentType(),m.getPath()));

          	List<String> entityCodes = moduleItems.stream()
					.filter(item -> CET_CLASS.equals(item.getItemClass()))
					.map(entity -> entity.getItemCode())
					.collect(Collectors.toList());
			log.info("entityCodes: {}", entityCodes);

			try {
				// using user role and permissions, figure out which entities are allowed to be exported
				log.info("user.getRoles(): {}", user.getRoles());
				List<String> permissions = user.getRoles().stream().filter(role -> role.startsWith("CE_"))
						.collect(Collectors.toList());
				log.info("permissions: {}", permissions);

				List<String> allowedEntities =
						entityCodes.stream()
								.filter(entityCode -> permissions.stream()
										.anyMatch(permission -> permission.contains(entityCode)))
								.collect(Collectors.toList());
				log.info("allowedEntities: {}", allowedEntities);

				List<EntityPermission> entityPermissions = allowedEntities.stream()
						.map(entityCode -> {
							List<String> permissionList = permissions.stream()
									.filter(permission -> permission.contains(entityCode))
									.map(permission -> permission.substring(permission.indexOf("-") + 1))
									.sorted()
									.collect(Collectors.toList());
							return new EntityPermission(entityCode, permissionList);
						})
						.collect(Collectors.toList());
				log.info("entityPermissions: {}", entityPermissions);

			} catch (Exception exception) {
				throw new BusinessException(exception);
			}
		} else {
			throw new BusinessException("Module not found: " + moduleCode);
		}
	}	
}

class EntityPermission {
	private String entityCode;
	private List<String> permissions;

	public EntityPermission(String entityCode, List<String> permissions) {
		this.entityCode = entityCode;
		this.permissions = permissions;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public List<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}

	@Override
	public String toString() {
		return "EntityPermission [entityCode=" + entityCode + ", permissions=" + permissions + "]";
	}
}