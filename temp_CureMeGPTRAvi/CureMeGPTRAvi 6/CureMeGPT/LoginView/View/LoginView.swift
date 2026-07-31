//
//  LoginView.swift
//  CureMeGPT
//
//  Created by YATIN  KALRA on 01/12/25.
//

import SwiftUI



struct LoginView: View {
    @State private var email: String = ""
    @State private var password: String = ""
    @State private var isPasswordVisible: Bool = false
    @EnvironmentObject private var coordinator: Coordinator
    @StateObject private var viewModel = LoginViewModel()
    
    enum Field {
        case email
        case password
    }

    @FocusState private var focusedField: Field?
    
    var body: some View {
        ZStack{
            VStack(spacing: 0) {
                // MARK: - TOP GRADIENT SECTION
                VStack(alignment: .leading, spacing: 10) {
                    
                    Text("Welcome Back!")
                        .font(.custom("Urbanist-Bold", size: 24))
                        .foregroundColor(.white)
                    
                    Text("Sign in to continue your dental health journey.")
                    
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
                
               ScrollView {
                // MARK: - INPUT FIELDS
                VStack(spacing: 25) {
                    
                    // Email Field
                    HStack(spacing: 16) {
                        Image("EmailImg")
                            .resizable()
                            .frame(width: 50, height: 50)
                        
                        TextField("Email / Phone Number", text: $viewModel.emailPhone)
                            .font(.custom("Urbanist-Regular", size: 16))
                            .focused($focusedField, equals: .email)
                            .padding(.horizontal, 16)
                            .frame(height: 50)
                            .background(
                                RoundedRectangle(cornerRadius: 25)
                                    .stroke(Color.gray.opacity(0.3), lineWidth: 1)
                            )
                    }
                    .padding(.horizontal, 24)
                    
                    // Password Field
                    HStack(spacing: 16) {
                        Image("LockImg")
                            .resizable()
                            .frame(width: 50, height: 50)
                        
                        HStack {
                            if isPasswordVisible {
                                TextField("Password", text: $viewModel.password)
                                    .font(.custom("Urbanist-Regular", size: 16))
                                    .focused($focusedField, equals: .password)
                                
                            } else {
                                SecureField("Password", text: $viewModel.password)
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
                    .padding(.horizontal, 24)
                    
                    // Forgot Password
                    HStack {
                        Spacer()
                        Button {
                            coordinator.push(.resetPasswordView)
                        } label: {
                            Text("Forgot Password?")
                                .font(.custom("Urbanist-Medium", size: 18))
                                .foregroundColor(Color(red: 24/255, green: 27/255, blue: 26/255))
                        }
                    }
                    .padding(.horizontal, 25)
                }
                .padding(.top, 70)
                
                // MARK: - LOGIN BUTTON
                Button {
                    viewModel.loginAPI(password: viewModel.password, emailPhone: viewModel.emailPhone) { success in
                        
                        if success {
                            coordinator.push(.tabBarView)
                        }
                    }
                } label: {
                    Text("Login")
                        .font(.custom("Urbanist-SemiBold", size: 16))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(
                              Image("BackgroundBtn") // Asset name
                                  .resizable()
                                  .scaledToFill()
                          )
//                        .background(
//                            LinearGradient(
//                                colors: [
//                                    Color(red: 67/255, green: 56/255, blue: 202/255),
//                                    Color(red: 33/255, green: 28/255, blue: 100/255)
//                                ],
//                                startPoint: .leading,
//                                endPoint: .trailing
//                            )
//                        )
                        .cornerRadius(30)
                        .padding(.horizontal, 24)
                        .padding(.top, 20)
                }
                .onChange(of: viewModel.loginSuccess) { oldValue, newValue in
                    if newValue {
                        coordinator.push(.tabBarView)
                        viewModel.loginSuccess = false
                    }
                }
                
                Spacer()
                
                // MARK: - Bottom Signup Text
                HStack {
                    Text("New here?")
                        .font(.custom("Urbanist-Medium", size: 18))
                    
                    Button("Create an account") {
                        coordinator.push(.createAccountView)
                    }
                    
                    .font(.custom("Urbanist-Medium", size: 18))
                    .foregroundColor(Color(red: 67/255, green: 56/255, blue: 202/255))
                }
                .padding(.horizontal, 60)
                .padding(.top, 200)
                .padding(.bottom, 40)
            }
               
        }
            if viewModel.showActivity {
                CustomLoderView(isVisible: $viewModel.showActivity)
                    .ignoresSafeArea()
            }
            
        }
        
        .customAlert(
            isPresented: $viewModel.isPresentAlert,
            message: viewModel.errorMessage ?? ""
        ) {
            print("OK tapped")
        }
//        .alert(isPresented: $viewModel.isPresentAlert) {
//            Alert(title: Text(viewModel.errorMessage ?? ""))
//        }
        .ignoresSafeArea(edges: .top)
        .onAppear {
            viewModel.clearCredentials()
        }
    }
    
    // MARK: - ICON CIRCLE VIEW
    func iconCircle(systemImage: String) -> some View {
        ZStack {
            Circle()
                .fill(
                    LinearGradient(
                        colors: [
                            Color(red: 67/255, green: 56/255, blue: 202/255),
                            Color(red: 33/255, green: 28/255, blue: 100/255)
                        ],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    )
                )
                .frame(width: 50, height: 50)

            Image(systemImage) // <- Loads custom asset image
                .resizable()
                .scaledToFit()
                .frame(width: 22, height: 22)
                .foregroundColor(.white)
        }
    }
}

// Rounded corner shape
struct RoundedCornerShape: Shape {
    var corners: UIRectCorner
    var radius: CGFloat
    
    func path(in rect: CGRect) -> Path {
        let path = UIBezierPath(
            roundedRect: rect,
            byRoundingCorners: corners,
            cornerRadii: CGSize(width: radius, height: radius)
        )
        return Path(path.cgPath)
    }
}

#Preview {
    LoginView()
}

