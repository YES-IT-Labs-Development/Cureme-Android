////
////  CreateAccountView.swift
////  CureMeGpt APP
////
////  Created by YES IT Labs on 18/11/25.
////
//
import SwiftUI

extension View {
    
    func customAlert(
        isPresented: Binding<Bool>,
        message: String,
        buttonTitle: String = "OK",
        action: (() -> Void)? = nil
    ) -> some View {
        modifier(
            CustomAlertModifier(
                isPresented: isPresented,
                message: message,
                buttonTitle: buttonTitle,
                action: action
            )
        )
    }
}

struct CreateAccountView: View {
    @StateObject private var avatarVM = CreateAccountViewModel()
    @EnvironmentObject private var coordinator: Coordinator
    @State private var isPasswordVisible: Bool = false
    @State private var isconfirmPasswordVisible: Bool = false
    
    @FocusState private var focusedField: Field?
    
    enum Field: Hashable {
        case name, email, password, confirmPassword
    }
    
    var body: some View {
        ZStack {
            LinearGradient(
                colors: [.white, Color(.systemGray6)],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()
            
            VStack(spacing: 0) {
                // MARK: Header (Pinned)
                
                VStack(alignment: .leading, spacing: 10) {
                    
                    Text("Create Your Account")
                        .font(.custom("Urbanist-Bold", size: 24))
                        .foregroundColor(.white)
                    
                    Text("Join and let AI guide your dental & family health.")
                    
                        .font(.custom("Urbanist-Regular", size: 16))
                        .foregroundColor(.white)
                    
                }
                .padding(.horizontal, 24)
                .padding(.top, 70)
                .frame(maxWidth: .infinity, alignment: .leading)
                .frame(height: 225)
                .background(
                    LinearGradient(
                        colors: [
                            Color(red: 67/255, green: 56/255, blue: 202/255),
                            Color(red: 33/255, green: 28/255, blue: 100/255)
                        ],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    )
                )
                .clipShape(RoundedCornerShape(corners: [.bottomLeft, .bottomRight], radius: 25))
                
                // MARK: ScrollView with Reader
                ScrollViewReader { scrollViewProxy in
                    ScrollView(showsIndicators: false) {
                        VStack(spacing: 18) {
                            CustomTextField(icon: "PersonImg",
                                            placeholder: "Full Name",
                                            text: $avatarVM.fullName)
                            .focused($focusedField, equals: .name)
                            .id(Field.name)
                            
                            CustomTextField(icon: "EmailImg",
                                            placeholder: "Email/Phone",
                                            text: $avatarVM.emailOrPhone)
                            .focused($focusedField, equals: .email)
                            .id(Field.email)
                            
                            
                            // Password Field
                            
                            HStack(spacing: 16) {
                                Image("LockImg")
                                    .resizable()
                                    .frame(width: 50, height: 50)
                                
                                HStack {
                                    if isPasswordVisible {
                                        TextField("Password", text: $avatarVM.newpassword)
                                            .focused($focusedField, equals: .password)
                                            .font(.custom("Urbanist-Regular", size: 16))
                                    } else {
                                        SecureField("Password", text: $avatarVM.newpassword)
                                            .focused($focusedField, equals: .password)
                                            .font(.custom("Urbanist-Regular", size: 16))
                                    }
                                    
                                    Button {
                                        isPasswordVisible.toggle()
                                    } label: {
                                        Image(isPasswordVisible ? "OpenEye" : "ri_eye-off-line")
                                            .resizable()
                                            .scaledToFit()
                                            .frame(width: 20, height: 20)
                                    }
                                }
                                .padding(.horizontal, 16)
                                .frame(height: 50)
                                .background(
                                    RoundedRectangle(cornerRadius: 25)
                                        .stroke(Color(hex: "#D9D9D9"), lineWidth: 1)
                                )
                            }
                            .id(Field.password)
                            
                            // Confirm Password Field
                            
                            //                            // Password Field
                            HStack(spacing: 16) {
                                Image("LockImg")
                                    .resizable()
                                    .frame(width: 50, height: 50)
                                
                                HStack {
                                    if isconfirmPasswordVisible {
                                        TextField("Confirm Password", text: $avatarVM.confirmPassword)
                                            .focused($focusedField, equals: .confirmPassword)
                                            .font(.custom("Urbanist-Regular", size: 16))
                                    } else {
                                        SecureField("Confirm Password", text: $avatarVM.confirmPassword)
                                            .focused($focusedField, equals: .confirmPassword)
                                            .font(.custom("Urbanist-Regular", size: 16))
                                    }
                                    
                                    Button {
                                        isconfirmPasswordVisible.toggle()
                                    } label: {
                                        Image(isconfirmPasswordVisible ? "OpenEye" : "ri_eye-off-line")
                                            .resizable()
                                            .scaledToFit()
                                            .frame(width: 20, height: 20)
                                    }
                                }
                                .padding(.horizontal, 16)
                                .frame(height: 50)
                                .background(
                                    RoundedRectangle(cornerRadius: 25)
                                        .stroke(Color(hex: "#D9D9D9"), lineWidth: 1)
                                )
                            }
                            .id(Field.confirmPassword)
                        }
                        .padding(.top, 40)
                        .padding(.horizontal, 25)
                      
                        // MARK: Create Account Button
                        Button(action: {
                            avatarVM.createAccountAPI(
                                fullname: avatarVM.fullName,
                                password: avatarVM.newpassword,
                                emailPhone: avatarVM.emailOrPhone
                            ) { success in
                                
                                if success {
                                    coordinator.push(
                                        .verificationView(
                                            source: .createAccount, emailPhone: avatarVM.emailOrPhone
                                        )
                                    )
                                }
                            }
                        }){
                            Text("Sign Up")
                                .foregroundColor(.white)
                                .font(.custom("PlusJakartaSans-SemiBold", size: 16))
                                .fontWeight(.medium)
                                .frame(maxWidth: .infinity)
                                .frame(height: 50)
                                .background(
                                      Image("BackgroundBtn") // Asset name
                                          .resizable()
                                          .scaledToFill()
                                  )
//                                .background(
//                                    LinearGradient(
//                                        colors: [
//                                            Color(red: 67/255, green: 56/255, blue: 202/255),
//                                            Color(red: 33/255, green: 28/255, blue: 100/255)
//                                        ],
//                                        startPoint: .leading,
//                                        endPoint: .trailing
//                                    )
//                                )
                                .cornerRadius(40)
                        }
                        .id("SignUpButton")
                        .padding(.top, 30)
                        .padding(.leading, 10)
                        .padding(.horizontal, 25)
                        Spacer()
                        // MARK: Log In
                        HStack(spacing: 4) {
                            Text("Already have an account?")
                                .font(.custom("Urbanist-Medium", size: 18))
                                .foregroundColor(.black)
                            
                            Button(action: {
                                print("Log In")
                                coordinator.push(.login)
                               
                            }) {
                                Text("Login")
                                    .font(.custom("Urbanist-Medium", size: 18))
                                    .foregroundColor(Color(hex: "#1E3A8A"))
                            }
                        }
                        .padding(.horizontal, 60)
                        .padding(.top, 175)
                        .padding(.bottom, 40)
                    }
                    .onChange(of: focusedField) { newValue in
                        if let field = newValue {
                            withAnimation {
                                // Confirm password focus hone par buttons ko bhi scroll karke view me layega
                                if field == .confirmPassword {
                                    scrollViewProxy.scrollTo("SignUpButton", anchor: .bottom)
                                } else {
                                    scrollViewProxy.scrollTo(field, anchor: .center)
                                }
                            }
                        }
                    }
                }
            }
            if avatarVM.showActivity {
                      CustomLoderView(isVisible: $avatarVM.showActivity)
                          .ignoresSafeArea()
                  }
            
        }
        .customAlert(
            isPresented: $avatarVM.isPresentAlert,
            message: avatarVM.errorMessage ?? ""
        ) {
            print("OK tapped")
        }
        .ignoresSafeArea(edges: .top)
    }
}


#Preview {
    CreateAccountView()
}
