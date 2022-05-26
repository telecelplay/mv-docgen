package org.meveo.doc;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.technicalservice.endpoint.Endpoint;
import org.meveo.service.technicalservice.endpoint.EndpointService;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.model.technicalservice.endpoint.TSParameterMapping;
import org.meveo.model.module.MeveoModule;
import org.meveo.model.module.MeveoModuleItem;
import org.meveo.model.BusinessEntity;
import org.meveo.security.MeveoUser;
import org.meveo.service.admin.impl.MeveoModuleService;
import org.meveo.service.crm.impl.CurrentUserProducer;
import org.meveo.service.script.Script;
import org.meveo.model.scripts.Function;
import org.meveo.service.git.GitHelper;

import net.steppschuh.markdowngenerator.text.Text;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.table.TableRow;
import net.steppschuh.markdowngenerator.list.UnorderedList;

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

		if (module == null) {
          throw new BusinessException("{} module not found.",moduleCode);
        }
		
      	log.info("Module found: {}, desc: {}", module.getCode(),module.getDescription());
		Set<MeveoModuleItem> moduleItems = module.getModuleItems();
		moduleItems.stream().forEach(m -> log.info("module item code == {}, item class == {}",m.getItemCode(),m.getItemClass()));
		
      	//== loading module Readme.md and update
      	String filePath = "";
      	StringBuilder builder = new StringBuilder();
      	try{
          	File modulePath = GitHelper.getRepositoryDir(user,moduleCode);
          	log.info("root dir path == {}",modulePath.getPath());
          	filePath = modulePath+"/README.md";
    		String text = new String ( Files.readAllBytes( Paths.get(filePath) ));
      		log.info("Readme.md text == {}",text);
          	builder.append(new Heading(module.getCode(),1)).append("\n").append("\n");
          	builder.append(new Text(module.getDescription())).append("\n");
        } catch(IOException ex){
        	throw new BusinessException(ex);
        }
        List<String> endpointCodes = moduleItems.stream()
				.filter(item -> ENDPOINT_CLASS.equals(item.getItemClass()))
				.map(entity -> entity.getItemCode())
				.collect(Collectors.toList());
       	log.info("endpointCodes == {}",endpointCodes);
      	if(endpointCodes != null && endpointCodes.size()>0){
        }
		endpointCodes.forEach(c -> {
        	log.info("endpoint code == {}",c);
            Endpoint endpoint = endpointService.findByCode(c);
            if(endpoint == null){
            	log.info("endpoint not found");
            }
			//== adding Rest Service 
          	builder.append(new Heading("Rest Service",3)).append("\n");
          	
          	Table.Builder endpointTableBuilder = new Table.Builder().withAlignments(Table.ALIGN_RIGHT, Table.ALIGN_LEFT)
            	.withRowLimit(endpointCodes.size()).addRow("Name", "Endpoint URL","Method","Description");
            endpointTableBuilder.addRow(endpoint.getCode(),endpoint.getEndpointUrl(),endpoint.getMethod().getLabel(),endpoint.getDescription());
              	
            builder.append(new Text(endpointTableBuilder.build().toString())).append("\n");          	
            log.info("endpoint method == {}, content-type == {}, url == {}, ",endpoint.getMethod().getLabel(),endpoint.getContentType(),endpoint.getEndpointUrl());
            
          	//== endpoint input fields
          	log.info("total endpoint input fields size == {}",endpoint.getParametersMapping().size());
          	if(endpoint.getParametersMapping().size()>0){
            	//List<Object> items = new ArrayList();
    			//items.add("Input Fields");
          		//builder.append(new UnorderedList<>(items).toString()).append("\n");
              	builder.append(new Text("*Input Fields:")).append("\n");
              	Table.Builder inputFieldsTableBuilder = new Table.Builder().withAlignments(Table.ALIGN_RIGHT, Table.ALIGN_LEFT)
            		.withRowLimit(endpointCodes.size()).addRow("Object", "Type","Default Value","List Options","Obs / Conditions");

	            endpoint.getParametersMapping().forEach(f -> {
    	        	log.info("field name == {}",f.getParameterName());
                  	inputFieldsTableBuilder.addRow(f.getParameterName(),"",f.getDefaultValue(),"","");
        		});
				builder.append(new Text(inputFieldsTableBuilder.build().toString())).append("\n").append("\n");
            }
          
          	ScriptInstance scriptInstance = scriptInstanceService.findById(endpoint.getService().getId());
        	if(scriptInstance == null){
        		log.info("script instance is null");
        	} else {
        		log.info("script instance id == {}, code=={}, desc=={}",scriptInstance.getId(),scriptInstance.getCode(),scriptInstance.getDescription());
              	
              	builder.append(new Heading("Meveo Function",3)).append("\n");
          	
          		Table.Builder tableBuilder = new Table.Builder().withAlignments(Table.ALIGN_RIGHT, Table.ALIGN_LEFT)
            		.withRowLimit(endpointCodes.size()).addRow("Type", "Name","Path","Description");
              	tableBuilder.addRow("Meveo Function",scriptInstance.getCode(),"",scriptInstance.getDescription());
              	
              	builder.append(new Text(tableBuilder.build().toString())).append("\n");
            }

        });
         
        //List<String> scriptInstanceIds = moduleItems.stream()
		//		.filter(item -> SCRIPT_INSTANCE_CLASS.equals(item.getItemClass()))
		//		.map(entity -> entity.getService().getId())
		//		.collect(Collectors.toList());
		//scriptInstanceCodes.forEach(i -> {
        //  	log.info("scriptInstance id == {}",i);
        // 	ScriptInstance scriptInstance = scriptInstanceService.findById(i);
        //  	if(scriptInstance == null){
        //      log.info("scrip instance not found");
        //    }
            //log.info("endpoint method == {}, content-type == {}, url == {}, ",endpoint.getMethod().getLabel(),endpoint.getContentType(),endpoint.getEndpointUrl());
            //log.info("total endpoint input fields size == {}",endpoint.getParametersMapping().size());
            //endpoint.getParametersMapping().forEach(f -> {
            //  log.info("field name == {}",f.getParameterName());
            //});
        //});
          
        List<String> entityCodes = moduleItems.stream()
			.filter(item -> CET_CLASS.equals(item.getItemClass()))
			.map(entity -> entity.getItemCode())
			.collect(Collectors.toList());
		log.info("entityCodes: {}", entityCodes);
      
      	//== write to file
        writeToFile(filePath,builder.toString());
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
}
