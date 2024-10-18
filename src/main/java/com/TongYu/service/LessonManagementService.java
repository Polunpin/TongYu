package com.TongYu.service;

import com.TongYu.dto.PersonalInfoResponse;
import com.TongYu.model.Student;

public interface LessonManagementService {

    PersonalInfoResponse personalInfo(String unionId);

}
