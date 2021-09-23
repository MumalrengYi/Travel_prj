package kr.co.softcampus.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import kr.co.softcampus.beans.UserBean;

public class UserValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return UserBean.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		UserBean userBean = (UserBean) target;

		String beanName = errors.getObjectName();
		// 유효성 검사시에 UserBean을 사용하는 다른 서비스에서 걸러지기 떄문에
		// 그를 방지하고자 beanName으로 사전에 한번 걸러주게 된다.
		// UserBean : join에서 사용(joinUserBean), login에서 사용(tempLoginUserBean)

		if (beanName.equals("joinUserBean") || beanName.equals("modifyUserBean")) {
			if (userBean.getUser_pw().equals(userBean.getUser_pw2()) == false) {
				errors.rejectValue("user_pw", "NotEquals");
				errors.rejectValue("user_pw2", "NotEquals");
			}

			if (beanName.equals("joinUserBean")) {

				if (userBean.isUserIdExist() == false) {
					errors.rejectValue("user_id", "DontCheckUserIdExist");
				}
			}
		}
	}

}
