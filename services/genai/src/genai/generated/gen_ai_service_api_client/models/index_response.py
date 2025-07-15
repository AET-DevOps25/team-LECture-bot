from collections.abc import Mapping
from typing import Any, TypeVar, Union

from attrs import define as _attrs_define
from attrs import field as _attrs_field

from ..types import UNSET, Unset

T = TypeVar("T", bound="IndexResponse")


@_attrs_define
class IndexResponse:
    """
    Attributes:
        message (Union[Unset, str]):  Example: Document indexed successfully.
        document_id (Union[Unset, str]):  Example: doc-123.
        status (Union[Unset, str]): Status of the indexing operation. Example: completed.
        chunks_processed (Union[Unset, int]): Number of text chunks processed. Example: 5.
        chunks_stored_in_weaviate (Union[Unset, int]): Number of chunks successfully stored in Weaviate. Example: 5.
    """

    message: Union[Unset, str] = UNSET
    document_id: Union[Unset, str] = UNSET
    status: Union[Unset, str] = UNSET
    chunks_processed: Union[Unset, int] = UNSET
    chunks_stored_in_weaviate: Union[Unset, int] = UNSET
    additional_properties: dict[str, Any] = _attrs_field(init=False, factory=dict)

    def to_dict(self) -> dict[str, Any]:
        message = self.message

        document_id = self.document_id

        status = self.status

        chunks_processed = self.chunks_processed

        chunks_stored_in_weaviate = self.chunks_stored_in_weaviate

        field_dict: dict[str, Any] = {}
        field_dict.update(self.additional_properties)
        field_dict.update({})
        if message is not UNSET:
            field_dict["message"] = message
        if document_id is not UNSET:
            field_dict["document_id"] = document_id
        if status is not UNSET:
            field_dict["status"] = status
        if chunks_processed is not UNSET:
            field_dict["chunks_processed"] = chunks_processed
        if chunks_stored_in_weaviate is not UNSET:
            field_dict["chunks_stored_in_weaviate"] = chunks_stored_in_weaviate

        return field_dict

    @classmethod
    def from_dict(cls: type[T], src_dict: Mapping[str, Any]) -> T:
        d = dict(src_dict)
        message = d.pop("message", UNSET)

        document_id = d.pop("document_id", UNSET)

        status = d.pop("status", UNSET)

        chunks_processed = d.pop("chunks_processed", UNSET)

        chunks_stored_in_weaviate = d.pop("chunks_stored_in_weaviate", UNSET)

        index_response = cls(
            message=message,
            document_id=document_id,
            status=status,
            chunks_processed=chunks_processed,
            chunks_stored_in_weaviate=chunks_stored_in_weaviate,
        )

        index_response.additional_properties = d
        return index_response

    @property
    def additional_keys(self) -> list[str]:
        return list(self.additional_properties.keys())

    def __getitem__(self, key: str) -> Any:
        return self.additional_properties[key]

    def __setitem__(self, key: str, value: Any) -> None:
        self.additional_properties[key] = value

    def __delitem__(self, key: str) -> None:
        del self.additional_properties[key]

    def __contains__(self, key: str) -> bool:
        return key in self.additional_properties
