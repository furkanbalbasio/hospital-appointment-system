package org.has.service;

import lombok.RequiredArgsConstructor;
import org.has.dto.request.DoctorSaveRequestDto;
import org.has.dto.request.LoginRequestDto;
import org.has.dto.response.DoctorFindallResponseDto;
import org.has.exception.AdminException;
import org.has.exception.ErrorType;
import org.has.manager.DoctorManager;
import org.has.repository.AdminRepository;
import org.has.repository.entity.Admin;
import org.has.utility.JwtTokenManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final JwtTokenManager jwtTokenManager;
    private final DoctorManager doctorManager;

    public String login(LoginRequestDto dto) {
        Optional<Admin> auth = adminRepository.findOptionalByUsernameAndPassword(dto.getUsermame(),dto.getPassword());
        if(auth.isEmpty()) throw new AdminException(ErrorType.USERNAME_PASSWORD_ERROR);

        Optional<String> jwtToken = jwtTokenManager.createToken(auth.get().getId());
        if(jwtToken.isEmpty())
            throw new AdminException(ErrorType.TOKEN_ERROR);
        return jwtToken.get();
    }
    public void createDoctor(String token,DoctorSaveRequestDto dto) {
        boolean isAdmin=jwtTokenManager.validateToken(token);
        if (isAdmin){
            doctorManager.save(dto);
        }else {
            throw new AdminException(ErrorType.USERNAME_PASSWORD_ERROR);

        }
    }

    public List<DoctorFindallResponseDto> findAllDoctor(String token) {
        boolean isAdmin=jwtTokenManager.validateToken(token);
        if (!isAdmin){
            throw new AdminException(ErrorType.USERNAME_PASSWORD_ERROR);
        }
        return (List<DoctorFindallResponseDto>) doctorManager.findall();
    }
}
