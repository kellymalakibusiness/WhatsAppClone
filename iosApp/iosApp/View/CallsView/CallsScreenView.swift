//
//  CallsScreenView.swift
//  iosApp
//
//  Created by Kelly Malaki on 12/12/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct CallsScreenView: View {
    @State var filterOnlyMissedCalls: Bool = false
    var body: some View {
        NavigationStack {
            List {
                Section(
                    header:
                        Text("Favourites")
                        .fontWeight(.semibold)
                        .font(.title2)
                        .foregroundStyle(.primary)
                        .textCase(.none)
                ){
                    Button(action: {}, label: {
                        Text("Add favourite")
                            .foregroundStyle(.primary)
                    })
                }
                Text("To place a WhatsApp voice or video call, tap + at the top and select a contact.")
                    .font(.title)
                    .listRowBackground(Color.clear)
            }
            .toolbar {
                ToolbarItem(placement: .principal) {
                    selector
                }
                ToolbarItem(placement: .navigationBarTrailing){
                    addButton
                }
            }
            .navigationTitle("Calls")
        }
    }
    
    var selector: some View {
        Picker(selection: $filterOnlyMissedCalls) {
            Text("All").tag(false)
            Text("Missed").tag(true)
        } label: {
            Text("All")
        }.pickerStyle(.segmented)
            .frame(width: 150)

    }
    
    var addButton: some View {
        Image(systemName: "plus")
            .font(.footnote)
            .fontWeight(.bold)
            .padding(7)
            .foregroundColor(.primary)
            .background {
                Circle()
                    .fill(.secondary.opacity(0.15))
            }
    }
}

#Preview {
    CallsScreenView()
}
