//
//  MessageFilters.swift
//  iosApp
//
//  Created by Kelly Malaki on 13/12/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct MessageFilters: View {
    let title: String
    let isSelected: Bool
    
    let isIcon: Bool
    var backgroundColor: Color {
        if(isSelected) {
            Color("IconGreen").opacity(0.15)
        } else {
            .secondary.opacity(0.15)
        }
    }

    var body: some View {
        if(isIcon) {
            Image(systemName: title)
                .foregroundColor(isSelected ? Color("IconGreen") : .secondary)
                .padding(7)
                .background {
                    Circle()
                        .fill(backgroundColor)
                }
        } else {
            Text(title)
                .font(.system(size: 15, weight: .semibold))
                .foregroundColor(isSelected ? Color("IconGreen") : .secondary)
                .padding(.horizontal, 10)
                .padding(.vertical, 10)
                .background {
                    Capsule()
                        .fill(backgroundColor)
                }
        }
            
    }
}
