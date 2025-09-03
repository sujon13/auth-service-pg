package example.demo.service;

import example.demo.model.Designation;
import example.demo.model.Office;
import example.demo.model.UserOffice;
import example.demo.model.UserOfficeResponse;
import example.demo.repository.UserOfficeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserOfficeService {
    private final UserOfficeRepository userOfficeRepository;
    private final DesignationService designationService;
    private final OfficeService officeService;

    public List<UserOffice> findByUserId(final int userId) {
        return userOfficeRepository.findByUserId(userId);
    }

    public List<UserOffice> findByUserIdList(List<Integer> userIds) {
        return userOfficeRepository.findByUserIdIn(userIds);
    }

    private UserOfficeResponse buildUserOfficeResponse(UserOffice userOffice,
                                                       Designation designation,
                                                       Office office) {
        return UserOfficeResponse.builder()
                .userId(userOffice.getUserId())
                .designationId(designation.getId())
                .designation(designation.getName())
                .designationFullName(designation.getFullName())
                .officeId(office.getId())
                .office(office.getName())
                .officeFullName(office.getFullName())
                .company(office.getCompany())
                .build();
    }

    private Map<Integer, Designation> getIdToDesignationMap(List<UserOffice> userOfficeList) {
        List<Integer> designationIds = userOfficeList.stream()
                .map(UserOffice::getDesignationId)
                .toList();

        return designationService.findByIds(designationIds)
                .stream()
                .collect(Collectors.toMap(Designation::getId, designation -> designation));
    }

    private Map<Integer, Office> getIdToOfficeMap(List<UserOffice> userOfficeList) {
        List<Integer> officeIds = userOfficeList.stream()
                .map(UserOffice::getOfficeId)
                .toList();

        return officeService.findByIds(officeIds)
                .stream()
                .collect(Collectors.toMap(Office::getId, office -> office));
    }

    public List<UserOfficeResponse> getUserOfficeResponses(List<Integer> userIds) {
        List<UserOffice> userOffices = findByUserIdList(userIds);

        Map<Integer, Designation> idToDesignationMap = getIdToDesignationMap(userOffices);
        Map<Integer, Office> idToOfficeMap = getIdToOfficeMap(userOffices);

        return userOffices.stream()
                .map(userOffice -> buildUserOfficeResponse(userOffice,
                        idToDesignationMap.get(userOffice.getDesignationId()),
                        idToOfficeMap.get(userOffice.getOfficeId()))
                ).toList();
    }

    public List<UserOfficeResponse> getUserOfficeResponse(final int userId) {
        return getUserOfficeResponses(List.of(userId));
    }
}
