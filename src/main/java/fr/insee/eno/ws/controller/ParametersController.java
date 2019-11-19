package fr.insee.eno.ws.controller;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import fr.insee.eno.parameters.OutFormat;
import fr.insee.eno.parameters.StudyUnit;
import fr.insee.eno.ws.service.ParameterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name="Parameters")
@RestController
@RequestMapping("/parameters")
public class ParametersController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParametersController.class);


	@Autowired
	private ParameterService parameterService;


	@Operation(description="Get default xml file parameters")
	@GetMapping(value="param", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<StreamingResponseBody> getDefaultParam() throws Exception {

		InputStream paramsInputStream = parameterService.getDefaultParametersIS();

		StreamingResponseBody stream = out -> out.write(IOUtils.toByteArray(paramsInputStream));

		return  ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"default-params.xml\"")
				.body(stream);
	}

	@Operation(description="Get default xml file parameters according to the outFormat")
	@GetMapping(value="out-param", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<StreamingResponseBody> getDefaultOutParam(
			@RequestParam StudyUnit studyUnit,
			@RequestParam OutFormat outFormat) throws Exception {
		File fileParam;

		switch (outFormat) {
		case FR:
			fileParam=parameterService.getDefaultCustomParametersFile(studyUnit, OutFormat.FR);
			break;
		case PDF:
			fileParam=parameterService.getDefaultCustomParametersFile(studyUnit, OutFormat.PDF);
			break;
		case JS:
			fileParam=parameterService.getDefaultCustomParametersFile(StudyUnit.DEFAULT, OutFormat.JS);
			break;
		case ODT:
			fileParam=parameterService.getDefaultCustomParametersFile(StudyUnit.DEFAULT, OutFormat.ODT);
			break;
		default:
			fileParam = File.createTempFile("default-param", ".xml");
			FileUtils.copyInputStreamToFile(parameterService.getDefaultParametersIS(), fileParam);
			break;
		}
		StreamingResponseBody stream = out -> out.write(Files.readAllBytes(fileParam.toPath()));

		return  ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"default-"+outFormat+"-params.xml\"")
				.body(stream);
	}



}