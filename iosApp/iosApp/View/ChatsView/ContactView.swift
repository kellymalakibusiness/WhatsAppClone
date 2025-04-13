//
//  ContantView.swift
//  iosApp
//
//  Created by Kelly Malaki on 14/12/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct ContactView: View {
    
    let contact: Contact?
    var body: some View {
        if let message = contact {
            messageView(message)
        } else {
            archiveView()
        }
    }
    
    func archiveView() -> some View {
        return Button(action: {}, label: {
            HStack {
                Image(systemName: "archivebox")
                    .frame(width: 60, height: 30)
                    .padding(.trailing, 10)
                Text("Archived")
                    .font(.system(size: 16, weight: .semibold))
            }
        })
    }
    
    func messageView(_ message: Contact) -> some View {
        return Button(action: {}, label: {
            HStack {
                drawCircleImage(image: message.image, width: 60)
                    .padding(.trailing, 10)
                
                VStack(alignment: .leading, spacing: 1){
                    HStack(alignment: .top) {
                        Text(message.title)
                            .font(.system(size: 17, weight: .semibold))
                        Spacer()
                        Text(message.lastMessageDate)
                            .font(.caption)
                            .foregroundStyle(message.unreadMessages == nil ? .secondary : Color("IconGreen"))
                    }
                    HStack {
                        Text(message.lastMessage)
                            .font(.callout)
                            .foregroundStyle(.secondary)
                            .lineLimit(2)
                            .multilineTextAlignment(.leading)
                        if let newMessages = message.unreadMessages {
                            Spacer()
                            Text("\(newMessages)")
                                .font(.caption)
                                .foregroundStyle(Color("WhiteBlackInverse"))
                                .padding(5)
                                .background(
                                    Circle()
                                        .fill(Color("IconGreen"))
                                )
                        }
                        
                        
                    }
                }
            }
        })
    }
}

#Preview {
    ContactView(contact: Contact(image: "kevin_durant", title: "Kevin Durant", lastMessage: "I'm Kevin Durant. You know who I am. Yeah we on the building but I'm tryna take it to the top floor. I guess drama makes it for a good content.😂", lastMessageDate: "Yesterday", unreadMessages: 1))
}
