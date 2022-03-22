package com.reservoir.datareservoir.api.v1.controller;

import com.reservoir.datareservoir.api.v1.assembler.CubeDataDisassembler;
import com.reservoir.datareservoir.api.v1.assembler.CubeDataModelAssembler;
import com.reservoir.datareservoir.api.v1.model.CubeDataModel;
import com.reservoir.datareservoir.api.v1.model.input.CubeDataInput;
import com.reservoir.datareservoir.api.v1.openapi.CubeDataControllerOpenApi;
import com.reservoir.datareservoir.core.security.ReservoirSecurity;
import com.reservoir.datareservoir.domain.filter.PropertiesFilter;
import com.reservoir.datareservoir.domain.model.CubeData;
import com.reservoir.datareservoir.domain.service.CubeDataService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/v1/cube-data")
public class CubeDataController implements CubeDataControllerOpenApi {

    private final CubeDataService cubeDataService;
    private final CubeDataModelAssembler cubeDataModelAssembler;
    private final CubeDataDisassembler cubeDataDisassembler;
    private final ReservoirSecurity reservoirSecurity;

    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CubeDataModel> getCubeData(PropertiesFilter propertiesFilter) {
        List<CubeData> cubeDataList = cubeDataService.getAll(propertiesFilter, reservoirSecurity.getAuthorities());
        return cubeDataModelAssembler.toCollectionModel(cubeDataList);
    }

    @Override
    @GetMapping(path = "{cubeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CubeDataModel getSingleCubeData(@PathVariable Long cubeId) throws AccessDeniedException {
        CubeData cubeData = cubeDataService.getOne(cubeId, reservoirSecurity.getAuthorities());
        return cubeDataModelAssembler.toModel(cubeData);
    }

    @Override
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void postCubeData(@RequestBody @Valid CubeDataInput cubeDataInput) {
        cubeDataService.save(cubeDataDisassembler.toDomainObject(cubeDataInput, reservoirSecurity.getClientId()));
    }

    @Override
    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCubeData(PropertiesFilter propertiesFilter) {
        cubeDataService.deleteMulti(propertiesFilter, reservoirSecurity.getAuthorities());
    }

    @Override
    @DeleteMapping(path = "{cubeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSingleCubeData(@PathVariable Long cubeId) {
        cubeDataService.deleteOne(cubeId);
    }
}
