
package com.production.maintenance.controller;

import com.production.maintenance.model.Machine;
import com.production.maintenance.model.ProductionRecord;
import com.production.maintenance.model.Breakdown;
import com.production.maintenance.model.MaintenanceRequest;

import com.production.maintenance.repository.MachineRepository;
import com.production.maintenance.repository.ProductionRecordRepository;
import com.production.maintenance.repository.BreakdownRepository;
import com.production.maintenance.repository.MaintenanceRequestRepository;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class CrudController {

    private final MachineRepository machineRepository;
    private final ProductionRecordRepository productionRepository;
    private final BreakdownRepository breakdownRepository;
    private final MaintenanceRequestRepository maintenanceRepository;

    public CrudController(
            MachineRepository machineRepository,
            ProductionRecordRepository productionRepository,
            BreakdownRepository breakdownRepository,
            MaintenanceRequestRepository maintenanceRepository) {
        this.machineRepository = machineRepository;
        this.productionRepository = productionRepository;
        this.breakdownRepository = breakdownRepository;
        this.maintenanceRepository = maintenanceRepository;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        List<Machine> machines = machineRepository.findAll();
        List<ProductionRecord> today =
                productionRepository.findByProductionDate(LocalDate.now());

        long running = machines.stream()
                .filter(m -> "RUNNING".equalsIgnoreCase(m.getStatus()))
                .count();

        long idle = machines.stream()
                .filter(m -> "IDLE".equalsIgnoreCase(m.getStatus()))
                .count();

        long broken = machines.stream()
                .filter(m -> "BREAKDOWN".equalsIgnoreCase(m.getStatus()))
                .count();

        int target = today.stream()
                .mapToInt(ProductionRecord::getTargetQuantity).sum();

        int actual = today.stream()
                .mapToInt(ProductionRecord::getActualQuantity).sum();

        double downtime = breakdownRepository.findAll().stream()
                .filter(b -> b.getDowntimeHours() != null)
                .mapToDouble(Breakdown::getDowntimeHours).sum();

        long openBreakdowns =
                breakdownRepository.countByStatusIgnoreCase("OPEN");

        long openMaintenance =
                maintenanceRepository.countByStatusIgnoreCase("OPEN");

        long overdueMaintenance = maintenanceRepository.findAll().stream()
                .filter(m -> m.getDueDate() != null)
                .filter(m -> m.getDueDate().isBefore(LocalDate.now()))
                .filter(m -> !"COMPLETED".equalsIgnoreCase(m.getStatus()))
                .count();

        double efficiency = target == 0 ? 0.0
                : Math.round(actual * 10000.0 / target) / 100.0;

        return Map.ofEntries(
                Map.entry("totalMachines", machines.size()),
                Map.entry("runningMachines", running),
                Map.entry("activeMachines", running),
                Map.entry("idleMachines", idle),
                Map.entry("breakdownMachines", broken),
                Map.entry("todayTarget", target),
                Map.entry("todayActual", actual),
                Map.entry("productionEfficiency", efficiency),
                Map.entry("totalDowntimeHours", downtime),
                Map.entry("openBreakdowns", openBreakdowns),
                Map.entry("openMaintenance", openMaintenance),
                Map.entry("overdueMaintenance", overdueMaintenance)
        );
    }

    // Machines
    @GetMapping("/machines")
    public List<Machine> getMachines() {
        return machineRepository.findAll();
    }

    @PostMapping("/machines")
    public Machine createMachine(@RequestBody Machine machine) {
        machine.setId(null);
        return machineRepository.save(machine);
    }

    @DeleteMapping("/machines/{id}")
    public void deleteMachine(@PathVariable Long id) {
        if (!machineRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Machine not found");
        }
        machineRepository.deleteById(id);
    }

    // Production
    @GetMapping("/production")
    public List<ProductionRecord> getProduction() {
        return productionRepository.findAll();
    }

    @PostMapping("/production")
    public ProductionRecord createProduction(
            @RequestBody ProductionRecord record) {
        record.setId(null);
        return productionRepository.save(record);
    }

    @DeleteMapping("/production/{id}")
    public void deleteProduction(@PathVariable Long id) {
        if (!productionRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Production record not found");
        }
        productionRepository.deleteById(id);
    }

    // Breakdowns
    @GetMapping("/breakdowns")
    public List<Breakdown> getBreakdowns() {
        return breakdownRepository.findAll();
    }

    @PostMapping("/breakdowns")
    public Breakdown createBreakdown(@RequestBody Breakdown breakdown) {
        breakdown.setId(null);
        return breakdownRepository.save(breakdown);
    }

    @DeleteMapping("/breakdowns/{id}")
    public void deleteBreakdown(@PathVariable Long id) {
        if (!breakdownRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Breakdown not found");
        }
        breakdownRepository.deleteById(id);
    }

    // Maintenance
    @GetMapping("/maintenance")
    public List<MaintenanceRequest> getMaintenance() {
        return maintenanceRepository.findAll();
    }

    @PostMapping("/maintenance")
    public MaintenanceRequest createMaintenance(
            @RequestBody MaintenanceRequest request) {
        request.setId(null);
        return maintenanceRepository.save(request);
    }

    @DeleteMapping("/maintenance/{id}")
    public void deleteMaintenance(@PathVariable Long id) {
        if (!maintenanceRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Maintenance request not found");
        }
        maintenanceRepository.deleteById(id);
    }
}