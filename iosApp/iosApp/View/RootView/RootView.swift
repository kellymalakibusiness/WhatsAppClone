import SwiftUI
//import shared

struct RootView: View {
    // MARK: PROPERTIES
	//let greet = Greeting().greet()
    //let x = SomeDummyKt.areYouCool()

    let selectionItems = [
        SelectionItem(isSelected: false, tag: 0),
        SelectionItem(isSelected: false, tag: 1),
        SelectionItem(isSelected: true, tag: 2),
        SelectionItem(isSelected: true, tag: 3),
        SelectionItem(isSelected: true, tag: 4)
    ]

    @State private var currentTag: Int = 0



    // MARK: BODY
	var body: some View {
        TabView(selection: $currentTag) {
            // MARK: UPDATES SCREEN
            UpdatesScreenView()
                .ourTabView(title: "Updates", selectedIconImage: "status_selected", unselectedIconImage: "status_unselected", tag: selectionItems[0].tag, currentTag: currentTag)

            // MARK: CALLS SCREEN
            CallsScreenView()
                .ourTabView(title: "Calls", selectedSystemIconImage: "phone.fill", unselectedIconImage: "phone", tag: selectionItems[1].tag, currentTag: currentTag)


            // MARK: COMMUNITIES SCREEN
            CommunitiesScreenView()
                .ourTabView(title: "Communities", selectedSystemIconImage: "person.3.fill", unselectedIconImage: "communities_unselected", tag: selectionItems[2].tag, currentTag: currentTag)

            // MARK: CHATS SCREEN
            ChatsScreenView()
            .ourTabView(title: "Chats", selectedSystemIconImage: "bubble.left.and.bubble.right", unselectedIconImage: "chat_unselected", tag: selectionItems[3].tag, currentTag: currentTag)

            // MARK: SETTINGS SCREEN
            SettingsScreenView()
                .ourTabView(title: "Settings", systemIconImage: "gear", tag: selectionItems[4].tag)
        }
	}

    // MARK: FUNCTIONS
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        RootView()
    }
}


struct SelectionItem{
    let isSelected: Bool
    let tag: Int
}
