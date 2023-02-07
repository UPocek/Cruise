import { ChatItemDTO } from './chat-item-dto';

export interface AllChatItemsDTO {
  chatItems: ChatItemDTO[];
  panicPageCount: number;
  ridePageCount: number;
}
