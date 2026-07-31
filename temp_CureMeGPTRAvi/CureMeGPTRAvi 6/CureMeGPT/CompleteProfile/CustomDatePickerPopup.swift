
//
//  CustomCalendarView.swift
//  CureMeGpt APP
//
//  Created by YES IT Labs on 17/11/25.
//

import SwiftUI

struct CustomCalendarView: View {
    
    // MARK: - PROPERTIES FROM PARENT
    @Binding var selectedDate: Date?
    var onClose: () -> Void
    let cellSize: CGFloat = 30
    
    // MARK: - STATES
    @State private var currentMonth: Int = 0
    @State private var showYearPicker = false
    @State private var selectedYear: Int = Calendar.current.component(.year, from: Date())

    var body: some View {
        
        VStack(spacing: 22) {
            
            // 🔵 HEADER
            HStack {
                HStack(spacing: 8) {
                    Image(systemName: "calendar")
                        .foregroundColor(Color(hex: "#3D2DB7"))
                    Text("Select Date")
                        .font(.system(size: 18, weight: .semibold))
                }
                
                Spacer()
                
                Button {
                    onClose()
                } label: {
                    ZStack {
                        Circle()
                            .fill(Color.white)
                            .frame(width: 38, height: 38)
                            .shadow(color: .black.opacity(0.1), radius: 2)
                        Image(systemName: "xmark")
                            .foregroundColor(.black)
                    }
                }
            }
            .padding(.horizontal)
            .padding(.top, 30)
            
            
            // 🔵 MONTH + YEAR SELECTOR
            HStack {
                Button {
                    showYearPicker.toggle()
                } label: {
                    HStack(spacing: 6) {
                        Text(dateString(format: "MMMM yyyy"))
                            .font(.system(size: 17, weight: .semibold))
                            .foregroundColor(.black)
                        
                        Image(systemName: "chevron.right")
                            .font(.system(size: 14))
                            .foregroundColor(Color(hex: "#3D2DB7"))
                    }
                }
                
                Spacer()
                
                Button {
                    currentMonth -= 1
                } label: {
                    arrowButton(imageName: "leftArrow") // Asset name
                    //arrowButton(system: "chevron.left")
                }
 
                Button {
                    let nextMonthDate = Calendar.current.date(byAdding: .month, value: currentMonth + 1, to: Date())!
                    
                    if nextMonthDate <= Date() {
                        currentMonth += 1
                    }
                } label: {
                    arrowButton(imageName: "rightArrow")
                  //  arrowButton(system: "chevron.right")
                }
            }
            .padding(.horizontal)
            
            
            // 🔵 WEEK ROW
            HStack {
                ForEach(["SUN","MON","TUE","WED","THU","FRI","SAT"], id: \.self) { day in
                    Text(day)
                        .font(.system(size: 11, weight: .semibold))
                        .foregroundColor(.gray)
                        .frame(maxWidth: .infinity)
                }
            }
            .padding(.horizontal)
 
            // 🔵 FIXED HEIGHT CALENDAR GRID (ALWAYS 6 ROWS)
            let columns = Array(repeating: GridItem(.flexible()), count: 7)
            
            LazyVGrid(columns: columns, spacing: 12) {
                ForEach(extractDates()) { value in
                    dateCell(value: value)
                        .frame(width: cellSize, height: cellSize)
                }
            }
            .frame(height: (cellSize * 6) + (12 * 5)) // Fixed 6-row height
            .padding(.horizontal)
            
            
            // 🔵 BUTTONS
            HStack(spacing: 20) {
                Spacer()
                
                // Cancel
                Button {
                    onClose()
                } label: {
                    Text("Cancel")
                        .font(.system(size: 16))
                        .foregroundColor(.black)
                        .frame(height: 48)
                        .frame(width: 100)
                      //  .frame(maxWidth: .infinity)
                        .background(
                            RoundedRectangle(cornerRadius: 30)
                                .stroke(Color.black.opacity(0.4), lineWidth: 1)
                        )
                }
                
                // Apply
                Button {
                    onClose()
                } label: {
                    Text("Apply")
                        .font(.system(size: 16, weight: .semibold))
                        .foregroundColor(.white)
                        .frame(height: 48)
                        .frame(width: 100)
                        //.frame(maxWidth: .infinity)
                        .background(
                            LinearGradient( colors: [
                                Color(red: 67/255, green: 56/255, blue: 202/255),
                                Color(red: 33/255, green: 28/255, blue: 100/255)
                            ], startPoint: .leading, endPoint: .trailing)
                        )
                        .cornerRadius(30)
                }
            }
            .padding(.horizontal)
            .padding(.bottom, 30)
            
        }
        .frame(maxWidth: 380)
        .background(Color.white)
        .cornerRadius(25)
        .shadow(radius: 10)
        .sheet(isPresented: $showYearPicker) {
            YearPickerView(selectedYear: $selectedYear) { year in
                let diff = year - Calendar.current.component(.year, from: Date())
                currentMonth = diff * 12
                showYearPicker = false
            }
        }
    }
    

    // MARK: - DATE CELL
@ViewBuilder
    func dateCell(value: CalendarDate) -> some View {
        if value.day == -1 {
            Text("").frame(width: 32, height: 32)
        } else {
            let isSelected = selectedDate.map { Calendar.current.isDate(value.date, inSameDayAs: $0) } ?? false
            
            let today = Calendar.current.startOfDay(for: Date())
            let cellDate = Calendar.current.startOfDay(for: value.date)
            let isFutureDate = cellDate > today

            Text("\(value.day)")
                .font(.system(size: 17))
                .foregroundColor(
                    isFutureDate ? .gray :
                    (isSelected ? .white : .black)
                )
                .frame(width: 32, height: 32)
                .background(
                    Circle().fill(isSelected && !isFutureDate ? Color(hex: "#3D2DB7") : Color.clear)
                )
                .onTapGesture {
                    if !isFutureDate {
                        selectedDate = value.date
                    }
                }
        }
    }
    
    
//    // MARK: - ARROWS
//    func arrowButton(system: String) -> some View {
//        ZStack {
//            RoundedRectangle(cornerRadius: 10)
//                .fill(Color(hex: "#E6E0FA"))
//                .frame(width: 32, height: 32)
//            Image(systemName: system)
//                .foregroundColor(Color(hex: "#3D2DB7"))
//        }
//    }
    
    // MARK: - ARROWS
    func arrowButton(imageName: String) -> some View {
        ZStack {
//            RoundedRectangle(cornerRadius: 10)
//                .fill(Color(hex: "#E6E0FA"))
//                .frame(width: 32, height: 32)

            Image(imageName)
                .resizable()
                .scaledToFit()
                .frame(width: 25, height: 35)
        }
    }
    
    
    // MARK: - DATE FORMAT
    func dateString(format: String) -> String {
        let calendar = Calendar.current
        let date = calendar.date(byAdding: .month, value: currentMonth, to: Date())!
        
        let formatter = DateFormatter()
        formatter.dateFormat = format
        return formatter.string(from: date)
    }
    
    
    // MARK: - DATE GRID
    func extractDates() -> [CalendarDate] {
        let calendar = Calendar.current
        let current = calendar.date(byAdding: .month, value: currentMonth, to: Date())!
        
        var days = current.getAllDates().map {
            CalendarDate(day: calendar.component(.day, from: $0), date: $0)
        }
        
        // Insert blanks for starting weekday
        let firstWeekday = calendar.component(.weekday, from: days.first!.date)
        for _ in 1..<firstWeekday {
            days.insert(CalendarDate(day: -1, date: Date()), at: 0)
        }
        
        return days
    }
}


// MARK: - CALENDAR MODEL
struct CalendarDate: Identifiable {
    var id = UUID()
    var day: Int
    var date: Date
}


// MARK: - DATE EXTENSIONS
extension Date {
    func getAllDates() -> [Date] {
        let calendar = Calendar.current
        let range = calendar.range(of: .day, in: .month, for: self)!
        
        return range.compactMap { day -> Date in
            var components = calendar.dateComponents([.year, .month], from: self)
            components.day = day
            return calendar.date(from: components)!
        }
    }
}




// MARK: - YEAR PICKER VIEW
struct YearPickerView: View {
    @Binding var selectedYear: Int
    var onYearSelected: (Int) -> Void
    
    var body: some View {
        NavigationView {
            List {
                ForEach(1980...2050, id: \.self) { year in
                    Button {
                        selectedYear = year
                        onYearSelected(year)
                    } label: {
                        HStack {
                            Text("\(year)")
                                .foregroundColor(.primary)
                            Spacer()
                            if selectedYear == year {
                                Image("Checkmark")
                                    .resizable()
                                    .frame(width: 24, height: 24)
                            }
                        }
                    }
                }
            }
            .navigationTitle("Select Year")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}


#Preview {
    CustomCalendarView(
        selectedDate: .constant(Date() as Date?),
        onClose: {}
    )
}



