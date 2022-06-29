package org.meveo.doc;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.HashSet;


import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.security.MeveoUser;
import org.meveo.commons.utils.StringUtils;

import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.technicalservice.endpoint.Endpoint;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.technicalservice.endpoint.TSParameterMapping;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.crm.custom.EntityCustomAction;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.module.MeveoModule;
import org.meveo.model.module.MeveoModuleItem;
import org.meveo.model.BusinessEntity;
import org.meveo.model.scripts.Function;

import org.meveo.service.technicalservice.endpoint.EndpointService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.EntityCustomActionService;
import org.meveo.service.admin.impl.MeveoModuleService;
import org.meveo.service.crm.impl.CurrentUserProducer;
import org.meveo.service.script.Script;
import org.meveo.service.git.GitHelper;
import org.meveo.service.git.GitClient;

import net.steppschuh.markdowngenerator.text.Text;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.table.TableRow;
import net.steppschuh.markdowngenerator.list.UnorderedList;
import net.steppschuh.markdowngenerator.link.Link;

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
  	private ScriptInstanceService scriptInstanceService = getCDIBean(ScriptInstanceService.class);
	private GitClient gitClient = getCDIBean(GitClient.class);
  	private CustomEntityTemplateService cetService =  getCDIBean(CustomEntityTemplateService.class);
  	private CustomFieldTemplateService cftService = getCDIBean(CustomFieldTemplateService.class);
	private EntityCustomActionService ecaService = getCDIBean(EntityCustomActionService.class);


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

		if (module == null) {
          throw new BusinessException("{} module not found.",moduleCode);
        }
		
      	log.info("Module found: {}, desc: {}", module.getCode(),module.getDescription());
		Set<MeveoModuleItem> moduleItems = module.getModuleItems();
		moduleItems.stream().forEach(m -> log.info("module item code == {}, item class == {}",m.getItemCode(),m.getItemClass()));
		
      	//== loading module Readme.md and update
      	File modulePath;
      	String filePath;
        String gitPath = module.getGitRepository().getRemoteOrigin();
        gitPath = StringUtils.isBlank(gitPath)? "https://github.com/telcelplay/"+moduleCode : gitPath.substring(0,gitPath.length()-4);
      	log.info("module git path == {}",gitPath);
      	StringBuilder builder = new StringBuilder();
      	try{
          	modulePath = GitHelper.getRepositoryDir(user,moduleCode);
          	filePath = modulePath+"/README.md";
    		String text = new String ( Files.readAllBytes( Paths.get(filePath) ));
          	builder.append(new Heading(module.getCode(),1)).append("\n");
          	builder.append(new Text(module.getDescription())).append("\n");
        } catch(IOException ex){
        	throw new BusinessException(ex);
        }
        List<String> endpointCodes = moduleItems.stream()
				.filter(item -> ENDPOINT_CLASS.equals(item.getItemClass()))
				.map(entity -> entity.getItemCode())
				.collect(Collectors.toList());
		//== module endpoints
      	for(String code : endpointCodes){
        	log.info("endpoint code == {}",code);
            Endpoint endpoint = endpointService.findByCode(code);
            if(endpoint == null){
            	log.error("endpoint not found");
            }
			//== generating Rest Service table
          	builder.append("\n");
          	builder.append(new Heading("Rest Service",2)).append("\n");
          	
          	Table.Builder endpointTableBuilder = new Table.Builder().withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT)
            	.withRowLimit(endpointCodes.size()+1).addRow("Name", "Endpoint URL","Method","Description");
            endpointTableBuilder.addRow(endpoint.getCode(),endpoint.getEndpointUrl(),endpoint.getMethod().getLabel(),endpoint.getDescription());
              	
            builder.append(new Text(endpointTableBuilder.build().toString())).append("\n").append("\n");          	
          	//== generating endpoint input fields
          	if(endpoint.getService().getInputs().size()>0){
              	builder.append(new Text("* Input Fields:")).append("\n").append("\n");
              	Table.Builder inputFieldsTableBuilder = new Table.Builder().withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT)
            		.withRowLimit(endpoint.getService().getInputs().size()+1).addRow("Object", "Type","Default Value","List Options","Obs / Conditions");

              	endpoint.getService().getInputs().forEach(f -> {
                  	TSParameterMapping param = findTSParameterMapping(endpoint.getParametersMapping(),f.getName());
                  	String defaultValue = (param == null)?"":param.getDefaultValue();
                  	inputFieldsTableBuilder.addRow(f.getName(),f.getType(),defaultValue,"","");
        		});
	
				builder.append(new Text(inputFieldsTableBuilder.build().toString())).append("\n").append("\n");
            }
			//== generating output field table          
          	if(endpoint.getService().getOutputs().size()>0){
              	builder.append(new Text("* Output Fields:")).append("\n").append("\n");
              	Table.Builder outputFieldsTableBuilder = new Table.Builder().withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT)
            		.withRowLimit(endpoint.getService().getOutputs().size()+1).addRow("Object", "Type","Description");
              	endpoint.getService().getOutputs().forEach( o -> {
                  outputFieldsTableBuilder.addRow(o.getName(),o.getType(),o.getDescription());
                });

				builder.append(new Text(outputFieldsTableBuilder.build().toString())).append("\n").append("\n");
            }	
          
          	//== generating Meveo function
          	ScriptInstance scriptInstance = scriptInstanceService.findById(endpoint.getService().getId());
        	if(scriptInstance == null){
        		log.error("script instance is null");
        	} else {         		     	
              	builder.append(new Heading("Meveo Function",3)).append("\n");
          		
          		Table.Builder tableBuilder = new Table.Builder().withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT)
            		.withRowLimit(2).addRow("Type", "Name","Path","Description");
              
              	String scriptPath = scriptInstance.getCode().replace(".","/");

              	String absScriptPath = module.getGitRepository().getDefaultBranch()+"/facets/java/"+scriptPath+".java";
              	String scriptFilePath = gitPath+"/blob/"+absScriptPath;
              	log.info("link path == {}",new Link(absScriptPath,scriptFilePath));
              	tableBuilder.addRow("Meveo Function",scriptInstance.getCode(),new Link(absScriptPath,scriptFilePath),scriptInstance.getDescription());

              	builder.append(new Text(tableBuilder.build().toString())).append("\n");
            }
        }
      	
      	//== generating testsuite
        String postmanDirPath = "/facets/postman/";
      	String postmanGitPath = module.getGitRepository().getDefaultBranch()+postmanDirPath;
      	File postmanDir = new File(modulePath+postmanDirPath);
      	if(postmanDir.isDirectory()){
          	log.info("postman dir found");
          	File[] tests = postmanDir.listFiles();
          	if(tests.length>0){
              	builder.append("\n");
            	builder.append(new Heading("Postman Tests ",2)).append("\n");          		
        		Table.Builder postmanTableBuilder = new Table.Builder().withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT)
        			.withRowLimit(tests.length+1).addRow("Path");
              	for(File test: tests){
                	String gitLinkText = postmanGitPath+test.getName();
                  	String gitLinkPath = gitPath+"/blob/"+gitLinkText;
        			postmanTableBuilder.addRow(new Link(gitLinkText,gitLinkPath));
                }
        		builder.append(new Text(postmanTableBuilder.build().toString())).append("\n");
            }
        }
      	//== CETs
        List<String> entityCodes = moduleItems.stream()
			.filter(item -> CET_CLASS.equals(item.getItemClass()))
			.map(entity -> entity.getItemCode())
			.collect(Collectors.toList());
		log.info("entityCodes: {}", entityCodes);
      	builder.append(new Heading("ERD Diagram",2)).append("\n").append("\n");
      	builder.append(new Text("```mermaid")).append("\n");
      	builder.append(new Text("erDiagram")).append("\n");
      	for(String entityCode : entityCodes){
        	CustomEntityTemplate customEntityTemplate = cetService.findByCodeOrDbTablename(entityCode);
          	log.info(" CET name == {}",customEntityTemplate.getName());
          	log.info(" CET DB Table name == {}",customEntityTemplate.getDbTableName());
          	Map<String, CustomFieldTemplate> fields = cftService.findByAppliesTo(customEntityTemplate.getAppliesTo());
          	Map<String, EntityCustomAction> actions = ecaService.findByAppliesTo(customEntityTemplate.getAppliesTo());
			Set<String> refSchemaCodes = new HashSet();
          
          	//builder.append(new Text(customEntityTemplate.getDbTableName()));
			//FormFields formFields = new FormFields();
			for (Entry<String, CustomFieldTemplate> entry : fields.entrySet()) {
				CustomFieldTemplate field = entry.getValue();
				String fieldEntityCode = field.getEntityClazzCetCode();
              	log.info("CFT DBFieldName == {}, fieldEntityCode == {}, identifier == {}",field.getDbFieldname(),fieldEntityCode,field.isIdentifier());
              	if(fieldEntityCode != null){
					builder.append(new Text(customEntityTemplate.getDbTableName()+" ||--o{ "+fieldEntityCode+ " ")).append("\n");
                }
                //formFields.add(field);
				//boolean isEntity = fieldEntityCode != null;
				//if (isEntity && !fieldEntityCode.contains(".")) {
				//	refSchemaCodes.addAll(iterateRefSchemas(fieldEntityCode, refSchemaCodes));
				//}
			}
          	log.info("refSchemaCodes size == {}",refSchemaCodes.size());
          
          	//EntityActions entityActions = new EntityActions();
			for (Entry<String, EntityCustomAction> entry : actions.entrySet()) {
				log.debug("action: {}", entry.getKey());
				//entityActions.add(entry.getValue());
			}
        }
      	builder.append(new Text("```")).append("\n");
      
      	//== write to file
        writeToFile(filePath,builder.toString());
      	gitClient.commitFiles(module.getGitRepository(),List.of(new File(filePath)),"updating doc file.");
	}	
  
  	private void writeToFile(String filePath,String text){
      try{
            //== updating the file
          	FileWriter myWriter = new FileWriter(new File(filePath));
      		myWriter.write(text);
      		myWriter.close();
        } catch(IOException ex){
        	log.error(ex.getMessage());
        }
    }
  
  	private TSParameterMapping findTSParameterMapping(List<TSParameterMapping> params, String fieldName){
      Optional<TSParameterMapping> param = params.stream().filter(p -> p.getParameterName().equals(fieldName)).findFirst();
      return param.isPresent()?param.get():null;
    }
    
  
  	private Set<String> iterateRefSchemas(String entityCode, Set<String> allSchemas) {
      	if (allSchemas.contains(entityCode)) {
          return allSchemas;
        }
		Set<String> refSchemaCodes = allSchemas;
		refSchemaCodes.add(entityCode);
		CustomEntityTemplate entityTemplate = cetService.findByCodeOrDbTablename(entityCode);
		Map<String, CustomFieldTemplate> fields = cftService.findByAppliesTo(entityTemplate.getAppliesTo());
		for (Entry<String, CustomFieldTemplate> entry : fields.entrySet()) {
			String key = entry.getKey();
			CustomFieldTemplate field = entry.getValue();
			String fieldEntityCode = field.getEntityClazzCetCode();
			boolean isEntity = fieldEntityCode != null;
			boolean isAdded = refSchemaCodes.contains(key);
			boolean isCet = isEntity && !fieldEntityCode.contains(".");
			if (!isAdded && isCet) {
				log.debug("Adding to all schemas: {}", refSchemaCodes);
				refSchemaCodes.addAll(iterateRefSchemas(fieldEntityCode, refSchemaCodes));
			}
		}
		log.debug("Added Schemas: {}", refSchemaCodes);
		return refSchemaCodes;
	}
}
