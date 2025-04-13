//
//  CommonFunctions.swift
//  iosApp
//
//  Created by Kelly Malaki on 16/12/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI


func searchView(_ searchText: Binding<String>) -> some View {
    return HStack {
        Image(systemName: "magnifyingglass").foregroundColor(.secondary)
        TextField("Search", text: searchText)
    }.padding(10)
        .background(.secondary.opacity(0.15))
        .cornerRadius(10)
        .padding(.vertical, 5)
        .padding(.horizontal)
}

func drawIconImage(image: String, size: CGFloat) -> some View {
    return Image(image)
        .resizable()
        .frame(width: size, height: size)
        .aspectRatio(contentMode: .fit)
        .cornerRadius(10)
}

func drawCircleImage(image: String, width: CGFloat) -> some View {
    return Image(image)
        .resizable()
        .aspectRatio(contentMode: .fill)
        .frame(width: width, height: width)
        .cornerRadius(width/2)
}

func generateDateComposable(date: String, hasUnreadMessage: Bool) -> some View {
    return Text(date)
        .font(.caption)
        .foregroundStyle(
            hasUnreadMessage ? Color("IconGreen") : .secondary
        )
}
